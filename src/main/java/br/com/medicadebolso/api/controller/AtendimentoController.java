package br.com.medicadebolso.api.controller;

import br.com.medicadebolso.domain.dto.AtendimentoDTO;
import br.com.medicadebolso.domain.dto.AtendimentoResponseDTO;
import br.com.medicadebolso.domain.dto.MensagemDTO;
import br.com.medicadebolso.domain.service.AtendimentoService;
import br.com.medicadebolso.domain.service.MensagemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/atendimentos")
public class AtendimentoController {

    @Autowired
    private AtendimentoService atendimentoService;

    @Autowired
    private MensagemService mensagemService;

    @PostMapping
    public ResponseEntity<AtendimentoResponseDTO> criarAtendimento(@RequestBody AtendimentoDTO dto) {
        AtendimentoResponseDTO response = atendimentoService.criarAtendimento(dto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{atendimentoId}/iniciar")
    public ResponseEntity<AtendimentoResponseDTO> iniciarAtendimento(@PathVariable Long atendimentoId) {
        AtendimentoResponseDTO response = atendimentoService.iniciarAtendimento(atendimentoId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{atendimentoId}/finalizar")
    public ResponseEntity<AtendimentoResponseDTO> finalizarAtendimento(@PathVariable Long atendimentoId) {
        AtendimentoResponseDTO response = atendimentoService.finalizarAtendimento(atendimentoId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{atendimentoId}/cancelar")
    public ResponseEntity<AtendimentoResponseDTO> cancelarAtendimento(@PathVariable Long atendimentoId) {
        AtendimentoResponseDTO response = atendimentoService.cancelarAtendimento(atendimentoId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/medico/{medicoId}")
    public ResponseEntity<List<AtendimentoResponseDTO>> listarAtendimentosPorMedico(
            @PathVariable Long medicoId) {
        List<AtendimentoResponseDTO> atendimentos = atendimentoService.listarAtendimentosPorMedico(medicoId);
        return ResponseEntity.ok(atendimentos);
    }

    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<List<AtendimentoResponseDTO>> listarAtendimentosPorPaciente(
            @PathVariable Long pacienteId) {
        List<AtendimentoResponseDTO> atendimentos = atendimentoService.listarAtendimentosPorPaciente(pacienteId);
        return ResponseEntity.ok(atendimentos);
    }

    @GetMapping("/{atendimentoId}")
    public ResponseEntity<AtendimentoResponseDTO> buscarAtendimento(@PathVariable Long atendimentoId) {
        AtendimentoResponseDTO atendimento = atendimentoService.buscarAtendimento(atendimentoId);
        return ResponseEntity.ok(atendimento);
    }

    @PostMapping("/{atendimentoId}/mensagens")
    public ResponseEntity<MensagemDTO> enviarMensagem(
            @PathVariable Long atendimentoId,
            @RequestBody MensagemDTO dto) {
        dto.setAtendimentoId(atendimentoId);
        MensagemDTO mensagem = mensagemService.enviarMensagem(dto);
        return ResponseEntity.ok(mensagem);
    }

    @GetMapping("/{atendimentoId}/mensagens")
    public ResponseEntity<List<MensagemDTO>> buscarMensagens(@PathVariable Long atendimentoId) {
        List<MensagemDTO> mensagens = mensagemService.buscarMensagensPorAtendimento(atendimentoId);
        return ResponseEntity.ok(mensagens);
    }

    @PostMapping("/{atendimentoId}/mensagens/marcar-lidas")
    public ResponseEntity<Void> marcarMensagensComoLidas(
            @PathVariable Long atendimentoId,
            @RequestParam Long usuarioId) {
        mensagemService.marcarMensagensComoLidas(atendimentoId, usuarioId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{atendimentoId}/mensagens/periodo")
    public ResponseEntity<List<MensagemDTO>> buscarMensagensPorPeriodo(
            @PathVariable Long atendimentoId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {
        List<MensagemDTO> mensagens = mensagemService.buscarMensagensPorPeriodo(atendimentoId, inicio, fim);
        return ResponseEntity.ok(mensagens);
    }
} 