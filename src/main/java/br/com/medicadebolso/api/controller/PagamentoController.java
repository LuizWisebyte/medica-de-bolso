package br.com.medicadebolso.api.controller;

import br.com.medicadebolso.domain.dto.PagamentoDTO;
import br.com.medicadebolso.domain.model.Pagamento;
import br.com.medicadebolso.domain.service.PagamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pagamentos")
public class PagamentoController {
    
    @Autowired
    private PagamentoService pagamentoService;
    
    @PostMapping
    public ResponseEntity<Pagamento> processarPagamento(@RequestBody PagamentoDTO pagamentoDTO) {
        Pagamento pagamento = pagamentoService.processarPagamento(pagamentoDTO);
        return ResponseEntity.ok(pagamento);
    }
    
    @GetMapping("/consulta/{consultaId}")
    public ResponseEntity<List<Pagamento>> buscarPagamentosPorConsulta(@PathVariable Long consultaId) {
        List<Pagamento> pagamentos = pagamentoService.buscarPagamentosPorConsulta(consultaId);
        return ResponseEntity.ok(pagamentos);
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Pagamento>> buscarPagamentosPorStatus(@PathVariable Pagamento.StatusPagamento status) {
        List<Pagamento> pagamentos = pagamentoService.buscarPagamentosPorStatus(status);
        return ResponseEntity.ok(pagamentos);
    }
    
    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<List<Pagamento>> buscarPagamentosPorPaciente(@PathVariable Long pacienteId) {
        List<Pagamento> pagamentos = pagamentoService.buscarPagamentosPorPaciente(pacienteId);
        return ResponseEntity.ok(pagamentos);
    }
    
    @PutMapping("/{pagamentoId}/cancelar")
    public ResponseEntity<Void> cancelarPagamento(@PathVariable Long pagamentoId) {
        pagamentoService.cancelarPagamento(pagamentoId);
        return ResponseEntity.ok().build();
    }
} 