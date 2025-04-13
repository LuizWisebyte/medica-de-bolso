package br.com.medicadebolso.api.controller;

import br.com.medicadebolso.domain.Mensagem;
import br.com.medicadebolso.domain.dto.MensagemDTO;
import br.com.medicadebolso.domain.service.MensagemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/mensagens")
public class MensagemController {

    @Autowired
    private MensagemService mensagemService;

    @PostMapping
    public ResponseEntity<MensagemDTO> enviarMensagem(@RequestBody MensagemDTO mensagemDTO) {
        return ResponseEntity.ok(mensagemService.enviarMensagem(mensagemDTO));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Mensagem>> buscarMensagensPorUsuario(@PathVariable UUID usuarioId) {
        return ResponseEntity.ok(mensagemService.buscarMensagensPorUsuario(usuarioId));
    }

    @GetMapping("/usuario/{usuarioId}/nao-lidas")
    public ResponseEntity<List<Mensagem>> buscarMensagensNaoLidasPorUsuario(@PathVariable UUID usuarioId) {
        return ResponseEntity.ok(mensagemService.buscarMensagensNaoLidasPorUsuario(usuarioId));
    }

    @GetMapping("/usuario/{usuarioId}/contagem-nao-lidas")
    public ResponseEntity<Long> contarMensagensNaoLidasPorUsuario(@PathVariable UUID usuarioId) {
        return ResponseEntity.ok(mensagemService.contarMensagensNaoLidasPorUsuario(usuarioId));
    }

    @PutMapping("/{mensagemId}/marcar-como-lida")
    public ResponseEntity<Void> marcarComoLida(@PathVariable UUID mensagemId) {
        mensagemService.marcarComoLida(mensagemId);
        return ResponseEntity.ok().build();
    }
} 