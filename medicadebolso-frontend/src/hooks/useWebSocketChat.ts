import { useState, useEffect, useRef, useCallback } from 'react';
import SockJS from 'sockjs-client';
import { Client, IMessage, StompSubscription, Frame } from '@stomp/stompjs';
import { Mensagem, EnviarMensagemData } from '../services/atendimentoService'; // Reutilizar tipo

const WS_ENDPOINT = process.env.NEXT_PUBLIC_WS_URL || 'http://localhost:8080/ws'; // Endpoint do backend

interface UseWebSocketChatOptions {
    atendimentoId: number | null;
    onMensagemRecebida: (mensagem: Mensagem) => void;
    onError?: (error: any) => void;
    authToken: string | null; // Para possível autenticação na conexão
}

export const useWebSocketChat = ({ 
    atendimentoId, 
    onMensagemRecebida, 
    onError,
    authToken 
}: UseWebSocketChatOptions) => {
    const [isConnected, setIsConnected] = useState(false);
    const clientRef = useRef<Client | null>(null);
    const subscriptionRef = useRef<StompSubscription | null>(null);

    // Função para conectar
    const connect = useCallback(() => {
        if (!atendimentoId || clientRef.current?.active) {
            console.log('[WS] Conexão não necessária ou já ativa.');
            return;
        }

        console.log(`[WS] Tentando conectar ao atendimento ${atendimentoId}...`);
        
        const stompClient = new Client({
            // Broker URL usando SockJS
            webSocketFactory: () => new SockJS(WS_ENDPOINT),
            connectHeaders: {
                // TODO: Enviar token JWT para autenticação no handshake WebSocket, se o backend suportar
                // Authorization: `Bearer ${authToken}`,
            },
            debug: (str: string) => {
                console.log('[STOMP]', str); // Logs de debug do STOMP
            },
            reconnectDelay: 5000, // Tentar reconectar a cada 5 segundos
            heartbeatIncoming: 4000,
            heartbeatOutgoing: 4000,
        });

        stompClient.onConnect = (frame: Frame) => {
            console.log(`[WS] Conectado ao STOMP para atendimento ${atendimentoId}:`, frame);
            setIsConnected(true);

            // Inscrever-se no tópico específico do atendimento
            // ATENÇÃO: Ajustar este tópico conforme a implementação REAL do backend
            // O controller atual envia para /topic/messages (user-specific), o que está errado.
            // O correto seria algo como /topic/chat/{atendimentoId}
            const topic = `/topic/chat/${atendimentoId}`; 
            // const userTopic = `/user/topic/messages`; // Alternativa se for user-specific
            
            console.log(`[WS] Inscrevendo-se no tópico: ${topic}`);
            subscriptionRef.current = stompClient.subscribe(topic, (message: IMessage) => {
                try {
                    console.log('[WS] Mensagem recebida:', message.body);
                    const mensagemRecebida: Mensagem = JSON.parse(message.body);
                    onMensagemRecebida(mensagemRecebida);
                } catch (e) {
                    console.error('[WS] Erro ao processar mensagem recebida:', e);
                    onError && onError(e);
                }
            });
             console.log('[WS] Inscrição realizada.');
        };

        stompClient.onStompError = (frame: Frame) => {
            console.error('[WS] Erro no broker STOMP:', frame.headers['message']);
            console.error('Detalhes do erro:', frame.body);
            onError && onError(new Error(frame.headers['message'] || 'Erro STOMP'));
            setIsConnected(false);
        };
        
        stompClient.onWebSocketError = (event: Event) => {
             console.error('[WS] Erro no WebSocket:', event);
             onError && onError(event);
             setIsConnected(false);
        };
        
        stompClient.onDisconnect = () => {
            console.log(`[WS] Desconectado do atendimento ${atendimentoId}.`);
            setIsConnected(false);
            subscriptionRef.current = null;
            // Pode adicionar lógica para tentar reconectar aqui, se necessário
        };

        clientRef.current = stompClient;
        stompClient.activate(); // Inicia a conexão

    }, [atendimentoId, onMensagemRecebida, onError, authToken]);

    // Função para desconectar
    const disconnect = useCallback(() => {
        if (clientRef.current?.active) {
            console.log('[WS] Desconectando...');
            clientRef.current.deactivate();
        }
        clientRef.current = null;
        setIsConnected(false);
        subscriptionRef.current = null;
    }, []);

    // Efeito para conectar/desconectar quando o atendimentoId mudar ou o componente desmontar
    useEffect(() => {
        if (atendimentoId && authToken) { // Só conecta se tiver ID e token
            connect();
        }

        // Função de limpeza para desconectar ao desmontar
        return () => {
            disconnect();
        };
    }, [atendimentoId, authToken, connect, disconnect]);

    // Função para enviar mensagem via WebSocket
    const enviarMensagem = useCallback((mensagemData: EnviarMensagemData) => {
        if (!clientRef.current?.connected || !atendimentoId) {
            console.error('[WS] Não conectado ou sem ID de atendimento para enviar mensagem.');
            onError && onError(new Error('Não conectado ao chat.'));
            return;
        }
        
        // Destino baseado no @MessageMapping do backend
        const destination = `/app/chat.send`;
        // Adicionar atendimentoId ao payload se necessário pelo backend
        const payload = JSON.stringify({ ...mensagemData, atendimentoId }); 
        
        console.log(`[WS] Enviando mensagem para ${destination}:`, payload);
        clientRef.current.publish({
            destination,
            body: payload,
            // headers: { 'priority': '9' } // Exemplo de header customizado
        });
    }, [atendimentoId, onError]);

    return { isConnected, enviarMensagem };
}; 