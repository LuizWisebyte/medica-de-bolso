import React, { useState, useEffect, useCallback, useRef } from 'react';
import { useRouter } from 'next/router';
import withAuth from '../../hocs/withAuth';
import { useAuth } from '../../contexts/AuthContext';
import { 
    getMensagensPorAtendimento, 
    Mensagem,
    EnviarMensagemData
} from '../../services/atendimentoService';
import { useWebSocketChat } from '../../hooks/useWebSocketChat';
import { format } from 'date-fns';

// Estilos básicos inline para o chat
const styles = {
    container: { maxWidth: '700px', margin: '30px auto', border: '1px solid #ccc', borderRadius: '8px', display: 'flex', flexDirection: 'column', height: '80vh' } as React.CSSProperties,
    header: { padding: '10px', borderBottom: '1px solid #eee', fontWeight: 'bold' } as React.CSSProperties,
    messagesContainer: { flexGrow: 1, overflowY: 'auto', padding: '10px' } as React.CSSProperties,
    messageBubble: { marginBottom: '10px', padding: '8px 12px', borderRadius: '15px', maxWidth: '70%' } as React.CSSProperties,
    myMessage: { backgroundColor: '#dcf8c6', alignSelf: 'flex-end', marginLeft: 'auto' } as React.CSSProperties,
    otherMessage: { backgroundColor: '#eee', alignSelf: 'flex-start', marginRight: 'auto' } as React.CSSProperties,
    messageContent: {} as React.CSSProperties,
    messageInfo: { fontSize: '0.75em', color: '#888', marginTop: '3px' } as React.CSSProperties,
    inputArea: { display: 'flex', padding: '10px', borderTop: '1px solid #eee' } as React.CSSProperties,
    input: { flexGrow: 1, padding: '10px', border: '1px solid #ccc', borderRadius: '20px', marginRight: '10px' } as React.CSSProperties,
    button: { padding: '10px 15px', borderRadius: '20px', cursor: 'pointer' } as React.CSSProperties,
};

const ChatPage: React.FC = () => {
    const router = useRouter();
    const { atendimentoId: atendimentoIdParam } = router.query;
    const atendimentoId = typeof atendimentoIdParam === 'string' ? parseInt(atendimentoIdParam, 10) : null;
    
    const { user, token: authToken } = useAuth(); // Pegar token para WS
    const [mensagens, setMensagens] = useState<Mensagem[]>([]);
    const [loadingHistorico, setLoadingHistorico] = useState(true); // Renomeado
    const [error, setError] = useState<string | null>(null);
    const [novaMensagem, setNovaMensagem] = useState('');
    const [isSending, setIsSending] = useState(false); // Mantido para feedback do botão
    
    const messagesEndRef = useRef<null | HTMLDivElement>(null); 

    // Callback para adicionar mensagens (do histórico ou WS)
    const adicionarMensagem = useCallback((msg: Mensagem) => {
        // Evitar duplicatas se o REST e o WS retornarem a mesma msg enviada
        setMensagens(prev => {
            if (prev.some(m => m.id === msg.id)) {
                return prev; // Já existe
            }
            return [...prev, msg];
        });
    }, []);

    // Usar o hook WebSocket
    const { isConnected, enviarMensagem: enviarMensagemWS } = useWebSocketChat({
        atendimentoId,
        onMensagemRecebida: adicionarMensagem, // Passa o callback
        onError: (wsError) => {
            console.error('Erro WebSocket reportado no componente:', wsError);
            setError('Erro na conexão do chat em tempo real.'); // Atualiza erro na UI
        },
        authToken: authToken, // Passa o token de autenticação
    });

    // Função para buscar histórico
    const fetchMensagens = useCallback(async () => {
        if (!atendimentoId || !user) return;
        setLoadingHistorico(true);
        setError(null);
        try {
            const data = await getMensagensPorAtendimento(atendimentoId);
            // Usar adicionarMensagem para popular inicialmente
            // Limpar antes para não duplicar se reconectar?
            setMensagens([]); // Limpa antes de carregar histórico
            data.forEach(adicionarMensagem); 
        } catch (err: any) {
            setError(err.message || 'Erro ao buscar histórico de mensagens.');
        } finally {
            setLoadingHistorico(false);
        }
    }, [atendimentoId, user, adicionarMensagem]);

    // Busca inicial do histórico
    useEffect(() => {
        if (atendimentoId) {
            fetchMensagens();
        }
        // A conexão WS é gerenciada pelo hook useWebSocketChat
    }, [atendimentoId, fetchMensagens]);

    // Scroll para a última mensagem
     useEffect(() => {
        messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
    }, [mensagens]);

    // Enviar mensagem via WebSocket
    const handleEnviarMensagem = async (e?: React.FormEvent) => {
        if (e) e.preventDefault();
        if (!novaMensagem.trim() || !atendimentoId || isSending || !isConnected) return;

        setIsSending(true);
        setError(null);
        const data: EnviarMensagemData = { conteudo: novaMensagem };
        
        try {
            enviarMensagemWS(data); // Chama a função do hook WebSocket
            setNovaMensagem(''); // Limpa input
            // Não adicionamos localmente aqui, esperamos receber via WS
        } catch (err: any) { // Este catch pode não ser necessário se o hook tratar erros
            setError(err.message || 'Erro ao enviar mensagem via WS.');
        } finally {
            setIsSending(false); // Mesmo que WS não dê erro síncrono
        }
    };

    if (!atendimentoId) {
        return <div>ID do atendimento inválido.</div>; 
    }

    // Mostrar loading apenas para o histórico inicial
    if (loadingHistorico) {
        return <div>Carregando histórico do chat...</div>;
    }

    return (
        <div style={styles.container}>
            <div style={styles.header}>
                Chat - Atendimento #{atendimentoId} 
                <span style={{ marginLeft: '15px', fontSize: '0.8em', color: isConnected ? 'green' : 'red' }}>
                    ({isConnected ? 'Conectado' : 'Desconectado'})
                </span>
                {error && <span style={{ marginLeft: '15px', fontSize: '0.8em', color: 'red' }}> - {error}</span>} 
            </div>
            
            <div style={styles.messagesContainer}>
                {mensagens.map((msg) => {
                    const isMyMessage = msg.remetenteId === user?.id;
                    return (
                        <div 
                            key={msg.id} 
                            style={{
                                ...styles.messageBubble,
                                ...(isMyMessage ? styles.myMessage : styles.otherMessage)
                            }}
                        >
                            <div style={styles.messageContent}>{msg.conteudo}</div>
                            <div style={styles.messageInfo}>
                                {format(new Date(msg.dataHoraEnvio), 'dd/MM HH:mm')}
                                {isMyMessage && msg.lida && ' (✓✓)'} 
                            </div>
                        </div>
                    );
                })}
                <div ref={messagesEndRef} />
            </div>

            <form style={styles.inputArea} onSubmit={handleEnviarMensagem}>
                <input 
                    type="text"
                    style={styles.input}
                    placeholder={isConnected ? "Digite sua mensagem..." : "Conectando ao chat..."}
                    value={novaMensagem}
                    onChange={(e) => setNovaMensagem(e.target.value)}
                    disabled={isSending || !isConnected} // Desabilitar se enviando ou não conectado
                />
                <button 
                    type="submit" 
                    style={styles.button} 
                    disabled={isSending || !novaMensagem.trim() || !isConnected}
                >
                    {isSending ? 'Enviando...' : 'Enviar'}
                </button>
            </form>
        </div>
    );
};

export default withAuth(ChatPage); 