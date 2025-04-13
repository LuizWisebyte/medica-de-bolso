package br.com.medicadebolso.domain.controller;

import br.com.medicadebolso.domain.model.Mensagem;
import br.com.medicadebolso.domain.dto.MensagemDTO;
import br.com.medicadebolso.domain.service.MensagemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mensagens")
@RequiredArgsConstructor
public class MensagemController {

    private final MensagemService mensagemService;

    @PostMapping
    public ResponseEntity<MensagemDTO> enviarMensagem(@RequestBody MensagemDTO mensagemDTO) {
        return ResponseEntity.ok(mensagemService.enviarMensagem(mensagemDTO));
    }

    @GetMapping("/usuario/{remetenteId}")
    public ResponseEntity<List<Mensagem>> buscarMensagensPorRemetente(@PathVariable Long remetenteId) {
        return ResponseEntity.ok(mensagemService.buscarMensagensPorRemetente(remetenteId));
    }

    @GetMapping("/usuario/{remetenteId}/nao-lidas")
    public ResponseEntity<List<Mensagem>> buscarMensagensNaoLidasPorRemetente(@PathVariable Long remetenteId) {
        return ResponseEntity.ok(mensagemService.buscarMensagensNaoLidasPorRemetente(remetenteId));
    }

    @GetMapping("/usuario/{remetenteId}/contagem-nao-lidas")
    public ResponseEntity<Long> contarMensagensNaoLidasPorRemetente(@PathVariable Long remetenteId) {
        return ResponseEntity.ok(mensagemService.contarMensagensNaoLidasPorRemetente(remetenteId));
    }

    @PutMapping("/{mensagemId}/marcar-como-lida")
    public ResponseEntity<Void> marcarComoLida(@PathVariable Long mensagemId) {
        mensagemService.marcarComoLida(mensagemId);
        return ResponseEntity.ok().build();
    }
} 