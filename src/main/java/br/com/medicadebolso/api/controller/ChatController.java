package br.com.medicadebolso.api.controller;

import br.com.medicadebolso.domain.dto.MensagemChatDTO;
import br.com.medicadebolso.domain.model.MensagemChat;
import br.com.medicadebolso.domain.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {
    
    @Autowired
    private ChatService chatService;
    
    @PostMapping("/mensagem")
    public ResponseEntity<MensagemChat> enviarMensagem(@RequestBody MensagemChatDTO mensagemDTO) {
        MensagemChat mensagem = chatService.enviarMensagem(mensagemDTO);
        return ResponseEntity.ok(mensagem);
    }
    
    @GetMapping("/consulta/{consultaId}")
    public ResponseEntity<List<MensagemChat>> buscarMensagens(@PathVariable Long consultaId) {
        List<MensagemChat> mensagens = chatService.buscarMensagens(consultaId);
        return ResponseEntity.ok(mensagens);
    }
    
    @PutMapping("/mensagem/{mensagemId}/lida")
    public ResponseEntity<Void> marcarComoLida(@PathVariable Long mensagemId) {
        chatService.marcarComoLida(mensagemId);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/consulta/{consultaId}/nao-lidas")
    public ResponseEntity<Long> contarMensagensNaoLidas(@PathVariable Long consultaId) {
        long count = chatService.contarMensagensNaoLidas(consultaId);
        return ResponseEntity.ok(count);
    }
    
    @MessageMapping("/chat.send")
    @SendTo("/topic/chat")
    public MensagemChat handleMessage(MensagemChatDTO mensagemDTO) {
        return chatService.enviarMensagem(mensagemDTO);
    }
} 