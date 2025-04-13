package br.com.medicadebolso.domain.service;

import br.com.medicadebolso.domain.dto.PagamentoDTO;
import br.com.medicadebolso.domain.model.Consulta;
import br.com.medicadebolso.domain.model.Pagamento;
import br.com.medicadebolso.domain.model.Paciente;
import br.com.medicadebolso.domain.repository.ConsultaRepository;
import br.com.medicadebolso.domain.repository.PagamentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PagamentoService {
    
    private final PagamentoRepository pagamentoRepository;
    private final ConsultaRepository consultaRepository;
    
    @Transactional
    public Pagamento processarPagamento(Consulta consulta, String metodoPagamento, String observacoes) {
        Pagamento pagamento = new Pagamento();
        pagamento.setConsulta(consulta);
        pagamento.setValor(BigDecimal.valueOf(consulta.getValor()));
        pagamento.setMetodoPagamento(metodoPagamento);
        pagamento.setObservacoes(observacoes);
        pagamento.setStatus(Pagamento.StatusPagamento.PROCESSANDO);
        pagamento.setCodigoTransacao(UUID.randomUUID().toString());
        
        // Simula processamento do pagamento
        try {
            Thread.sleep(2000); // Simula delay de processamento
            pagamento.setStatus(Pagamento.StatusPagamento.CONCLUIDO);
        } catch (InterruptedException e) {
            pagamento.setStatus(Pagamento.StatusPagamento.REJEITADO);
        }
        
        return pagamentoRepository.save(pagamento);
    }
    
    @Transactional
    public Pagamento processarPagamento(PagamentoDTO pagamentoDTO) {
        Consulta consulta = consultaRepository.findById(pagamentoDTO.getConsultaId())
                .orElseThrow(() -> new RuntimeException("Consulta não encontrada"));
                
        return processarPagamento(consulta, pagamentoDTO.getMetodoPagamento(), pagamentoDTO.getObservacoes());
    }
    
    public List<Pagamento> buscarPorStatus(Pagamento.StatusPagamento status) {
        return pagamentoRepository.findByStatus(status);
    }
    
    public List<Pagamento> buscarPorPaciente(Paciente paciente) {
        return pagamentoRepository.findByConsultaPacienteId(paciente.getId());
    }
    
    @Transactional
    public void cancelarPagamento(Pagamento pagamento) {
        pagamento.setStatus(Pagamento.StatusPagamento.CANCELADO);
        pagamentoRepository.save(pagamento);
    }
    
    @Transactional
    public void atualizarStatusDisponibilidade(Pagamento pagamento, Pagamento.StatusDisponibilidade status) {
        pagamento.setStatusDisponibilidade(status);
        pagamentoRepository.save(pagamento);
    }
    
    public List<Pagamento> buscarPagamentosPorConsulta(Long consultaId) {
        return pagamentoRepository.findByConsultaId(consultaId);
    }
    
    public List<Pagamento> buscarPagamentosPorStatus(Pagamento.StatusPagamento status) {
        return pagamentoRepository.findByStatus(status);
    }
    
    public List<Pagamento> buscarPagamentosPorPaciente(Long pacienteId) {
        return pagamentoRepository.findByConsultaPacienteId(pacienteId);
    }
    
    @Transactional
    public void cancelarPagamento(Long pagamentoId) {
        Pagamento pagamento = pagamentoRepository.findById(pagamentoId)
                .orElseThrow(() -> new RuntimeException("Pagamento não encontrado"));
        cancelarPagamento(pagamento);
    }
} 