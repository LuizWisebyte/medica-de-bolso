package br.com.medicadebolso.domain.repository;

import br.com.medicadebolso.domain.model.Consulta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ConsultaRepository extends JpaRepository<Consulta, Long> {
    
    List<Consulta> findByMedicoIdAndDataHoraBetween(Long medicoId, LocalDateTime inicio, LocalDateTime fim);
    
    List<Consulta> findByPacienteIdAndDataHoraBetween(Long pacienteId, LocalDateTime inicio, LocalDateTime fim);
    
    boolean existsByMedicoIdAndDataHora(Long medicoId, LocalDateTime dataHora);
} 