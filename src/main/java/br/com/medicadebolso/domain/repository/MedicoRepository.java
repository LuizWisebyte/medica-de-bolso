package br.com.medicadebolso.domain.repository;

import br.com.medicadebolso.domain.model.Medico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicoRepository extends JpaRepository<Medico, Long> {

    // Nenhum método customizado necessário por enquanto, 
    // pois o PrescricaoService usa apenas o findById herdado.

} 