package br.com.medicadebolso.api.controller;

import br.com.medicadebolso.domain.dto.ExtratoItemDTO;
import br.com.medicadebolso.domain.dto.ResgateResponseDTO;
import br.com.medicadebolso.domain.dto.SolicitarResgateDTO;
import br.com.medicadebolso.domain.service.FinanceiroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/financeiro")
public class FinanceiroController {

    @Autowired
    private FinanceiroService financeiroService;

    @GetMapping("/medico/{medicoId}/saldo")
    public ResponseEntity<BigDecimal> getSaldoDisponivel(@PathVariable Long medicoId) {
        BigDecimal saldo = financeiroService.getSaldoDisponivel(medicoId);
        return ResponseEntity.ok(saldo);
    }

    @PostMapping("/resgate")
    public ResponseEntity<ResgateResponseDTO> solicitarResgate(@RequestBody SolicitarResgateDTO dto) {
        ResgateResponseDTO response = financeiroService.solicitarResgate(dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/medico/{medicoId}/extrato")
    public ResponseEntity<List<ExtratoItemDTO>> getExtrato(
            @PathVariable Long medicoId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {
        List<ExtratoItemDTO> extrato = financeiroService.getExtrato(medicoId, inicio, fim);
        return ResponseEntity.ok(extrato);
    }
} 