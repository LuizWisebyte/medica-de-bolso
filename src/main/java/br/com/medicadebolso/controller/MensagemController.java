package br.com.medicadebolso.controller;

import br.com.medicadebolso.api.dto.MensagemDTO;
import br.com.medicadebolso.service.MensagemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<List<MensagemDTO>> buscarMensagensPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(mensagemService.buscarMensagensPorUsuario(usuarioId));
    }

    @PutMapping("/{mensagemId}/marcar-lida")
    public ResponseEntity<Void> marcarComoLida(@PathVariable Long mensagemId) {
        mensagemService.marcarComoLida(mensagemId);
        return ResponseEntity.ok().build();
    }
} 