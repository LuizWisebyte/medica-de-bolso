package br.com.medicadebolso.api.controller;

import br.com.medicadebolso.domain.dto.MensagemDTO;
import br.com.medicadebolso.domain.service.MensagemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private MensagemService mensagemService;

    @MessageMapping("/chat.send")
    public void sendMessage(@Payload MensagemDTO mensagemDTO) {
        // 1. Salvar a mensagem (persistência)
        MensagemDTO mensagemSalva = mensagemService.enviarMensagem(mensagemDTO);
        
        // 2. Determinar o tópico do chat específico
        // Assumindo que MensagemDTO tem o ID do atendimento
        Long atendimentoId = mensagemSalva.getAtendimentoId(); 
        if (atendimentoId == null) {
            // Logar um erro ou lançar exceção, pois não é possível rotear a mensagem
            System.err.println("Erro: Atendimento ID nulo na mensagem DTO. Não é possível enviar via WebSocket.");
            // Considerar notificar o remetente sobre a falha?
            return; 
        }
        String chatTopic = "/topic/chat/" + atendimentoId;

        // 3. Enviar a mensagem salva para o tópico do chat
        System.out.println("Enviando mensagem para o tópico: " + chatTopic);
        messagingTemplate.convertAndSend(chatTopic, mensagemSalva);
        System.out.println("Mensagem enviada.");

        // Comentado: Lógica anterior que enviava apenas para o usuário
        /*
        messagingTemplate.convertAndSendToUser(
            mensagemDTO.getUsuarioId().toString(),
            "/topic/messages",
            mensagemSalva
        );
        */
    }
} 