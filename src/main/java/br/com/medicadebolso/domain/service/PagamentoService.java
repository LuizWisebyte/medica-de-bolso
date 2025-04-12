package br.com.medicadebolso.domain.service;

import br.com.medicadebolso.domain.dto.PagamentoDTO;
import br.com.medicadebolso.domain.model.Consulta;
import br.com.medicadebolso.domain.model.Pagamento;
import br.com.medicadebolso.domain.repository.ConsultaRepository;
import br.com.medicadebolso.domain.repository.PagamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class PagamentoService {
    
    @Autowired
    private PagamentoRepository pagamentoRepository;
    
    @Autowired
    private ConsultaRepository consultaRepository;
    
    @Transactional
    public Pagamento processarPagamento(PagamentoDTO pagamentoDTO) {
        Consulta consulta = consultaRepository.findById(pagamentoDTO.getConsultaId())
                .orElseThrow(() -> new RuntimeException("Consulta não encontrada"));
        
        Pagamento pagamento = new Pagamento();
        pagamento.setConsulta(consulta);
        pagamento.setValor(pagamentoDTO.getValor());
        pagamento.setMetodoPagamento(pagamentoDTO.getMetodoPagamento());
        pagamento.setObservacoes(pagamentoDTO.getObservacoes());
        pagamento.setStatus(Pagamento.StatusPagamento.PROCESSANDO);
        pagamento.setCodigoTransacao(UUID.randomUUID().toString());
        
        // Aqui você implementaria a lógica de integração com o gateway de pagamento
        // Por exemplo, integração com PagSeguro, Mercado Pago, etc.
        
        // Simulando processamento bem-sucedido
        pagamento.setStatus(Pagamento.StatusPagamento.CONCLUIDO);
        consulta.setPago(true);
        
        consultaRepository.save(consulta);
        return pagamentoRepository.save(pagamento);
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
                
        if (pagamento.getStatus() != Pagamento.StatusPagamento.CONCLUIDO) {
            pagamento.setStatus(Pagamento.StatusPagamento.CANCELADO);
            pagamentoRepository.save(pagamento);
        } else {
            throw new RuntimeException("Não é possível cancelar um pagamento já concluído");
        }
    }
} 