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
        MensagemDTO mensagemSalva = mensagemService.enviarMensagem(mensagemDTO);
        messagingTemplate.convertAndSendToUser(
            mensagemDTO.getUsuarioId().toString(),
            "/topic/messages",
            mensagemSalva
        );
    }
} 