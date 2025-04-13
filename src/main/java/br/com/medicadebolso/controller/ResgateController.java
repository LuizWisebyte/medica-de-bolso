package br.com.medicadebolso.controller;

import br.com.medicadebolso.domain.Resgate;
import br.com.medicadebolso.service.ResgateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/resgates")
@RequiredArgsConstructor
public class ResgateController {

    private final ResgateService resgateService;

    @PostMapping
    public ResponseEntity<Resgate> solicitarResgate(@RequestBody Resgate resgate) {
        return ResponseEntity.ok(resgateService.solicitarResgate(resgate));
    }

    @PutMapping("/{id}/processar")
    public ResponseEntity<Resgate> processarResgate(@PathVariable Long id) {
        return ResponseEntity.ok(resgateService.processarResgate(id));
    }

    @PutMapping("/{id}/finalizar")
    public ResponseEntity<Resgate> finalizarResgate(@PathVariable Long id) {
        return ResponseEntity.ok(resgateService.finalizarResgate(id));
    }

    @PutMapping("/{id}/rejeitar")
    public ResponseEntity<Resgate> rejeitarResgate(@PathVariable Long id, @RequestParam String motivo) {
        return ResponseEntity.ok(resgateService.rejeitarResgate(id, motivo));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Resgate>> listarResgatesPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(resgateService.listarResgatesPorUsuario(usuarioId));
    }
} 