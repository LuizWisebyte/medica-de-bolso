package br.com.medicadebolso.domain.repository;

import br.com.medicadebolso.domain.model.Resgate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ResgateRepository extends JpaRepository<Resgate, Long> {

    // Métodos de busca específicos podem ser adicionados aqui depois, se necessário
    // Ex: List<Resgate> findByMedicoIdOrderByDataSolicitacaoDesc(Long medicoId);
    
    // Método para buscar resgates em um período para o extrato
    List<Resgate> findByMedicoIdAndDataSolicitacaoBetween(
            Long medicoId, LocalDateTime inicio, LocalDateTime fim);
} 