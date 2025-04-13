package br.com.medicadebolso.domain.repository;

import br.com.medicadebolso.domain.model.Atendimento;
import br.com.medicadebolso.domain.model.StatusAtendimento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AtendimentoRepository extends JpaRepository<Atendimento, Long> {
    
    List<Atendimento> findByMedicoIdAndStatus(Long medicoId, StatusAtendimento status);
    
    List<Atendimento> findByPacienteIdAndStatus(Long pacienteId, StatusAtendimento status);
    
    List<Atendimento> findByMedicoIdAndDataCriacaoBetween(
            Long medicoId, LocalDateTime inicio, LocalDateTime fim);
    
    List<Atendimento> findByPacienteIdAndDataCriacaoBetween(
            Long pacienteId, LocalDateTime inicio, LocalDateTime fim);
    
    List<Atendimento> findByStatusAndDataCriacaoBefore(
            StatusAtendimento status, LocalDateTime dataLimite);
} 