package br.com.medicadebolso.domain.repository;

import br.com.medicadebolso.domain.model.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {
    
    List<Pagamento> findByConsultaId(Long consultaId);
    
    List<Pagamento> findByStatus(Pagamento.StatusPagamento status);
    
    List<Pagamento> findByConsultaPacienteId(Long pacienteId);
} 