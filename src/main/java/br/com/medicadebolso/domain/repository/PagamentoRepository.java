package br.com.medicadebolso.domain.repository;

import br.com.medicadebolso.domain.model.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import br.com.medicadebolso.domain.model.Pagamento.StatusPagamento;
import br.com.medicadebolso.domain.model.Pagamento.StatusDisponibilidade;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {
    
    List<Pagamento> findByConsultaId(Long consultaId);
    
    List<Pagamento> findByStatus(Pagamento.StatusPagamento status);
    
    List<Pagamento> findByConsultaPacienteId(Long pacienteId);

    @Query("SELECT SUM(p.valorLiquido) FROM Pagamento p JOIN p.consulta c " +
           "WHERE c.medico.id = :medicoId AND p.statusDisponibilidade = :status")
    Optional<BigDecimal> findSaldoDisponivelByMedicoId(
            @Param("medicoId") Long medicoId,
            @Param("status") StatusDisponibilidade status);

    List<Pagamento> findByConsultaMedicoIdAndStatusDisponibilidade(
            Long medicoId, StatusDisponibilidade statusDisponibilidade);

    List<Pagamento> findByConsultaMedicoIdAndStatusPagamentoAndDataPagamentoBetween(
            Long medicoId, StatusPagamento statusPagamento, LocalDateTime inicio, LocalDateTime fim);
            
    List<Pagamento> findByStatusPagamentoAndStatusDisponibilidade(
            StatusPagamento statusPagamento, StatusDisponibilidade statusDisponibilidade);
} 