package br.com.medicadebolso.domain.repository;

import br.com.medicadebolso.domain.model.Prescricao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrescricaoRepository extends JpaRepository<Prescricao, Long> {

    // Busca prescrições associadas a um ID de consulta específico.
    List<Prescricao> findByConsultaId(Long consultaId);

    // Busca prescrições associadas a um ID de médico específico.
    List<Prescricao> findByMedicoId(Long medicoId);

    // Busca prescrições navegando pelo relacionamento Consulta -> Paciente.
    // Spring Data JPA infere a junção necessária baseado no nome do método.
    List<Prescricao> findByConsultaPacienteId(Long pacienteId);

} 