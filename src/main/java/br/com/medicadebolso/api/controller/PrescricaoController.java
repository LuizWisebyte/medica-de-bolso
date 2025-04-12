package br.com.medicadebolso.api.controller;

import br.com.medicadebolso.domain.dto.PrescricaoDTO;
import br.com.medicadebolso.domain.model.Prescricao;
import br.com.medicadebolso.domain.service.PrescricaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prescricoes")
public class PrescricaoController {
    
    @Autowired
    private PrescricaoService prescricaoService;
    
    @PostMapping
    public ResponseEntity<Prescricao> criarPrescricao(@RequestBody PrescricaoDTO prescricaoDTO) {
        Prescricao prescricao = prescricaoService.criarPrescricao(prescricaoDTO);
        return ResponseEntity.ok(prescricao);
    }
    
    @GetMapping("/consulta/{consultaId}")
    public ResponseEntity<List<Prescricao>> buscarPrescricoesPorConsulta(@PathVariable Long consultaId) {
        List<Prescricao> prescricoes = prescricaoService.buscarPrescricoesPorConsulta(consultaId);
        return ResponseEntity.ok(prescricoes);
    }
    
    @GetMapping("/medico/{medicoId}")
    public ResponseEntity<List<Prescricao>> buscarPrescricoesPorMedico(@PathVariable Long medicoId) {
        List<Prescricao> prescricoes = prescricaoService.buscarPrescricoesPorMedico(medicoId);
        return ResponseEntity.ok(prescricoes);
    }
    
    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<List<Prescricao>> buscarPrescricoesPorPaciente(@PathVariable Long pacienteId) {
        List<Prescricao> prescricoes = prescricaoService.buscarPrescricoesPorPaciente(pacienteId);
        return ResponseEntity.ok(prescricoes);
    }
} 