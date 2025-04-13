package br.com.medicadebolso.domain.repository;

import br.com.medicadebolso.domain.model.Mensagem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface MensagemRepository extends JpaRepository<Mensagem, Long> {
    List<Mensagem> findByRemetenteId(Long remetenteId);
    List<Mensagem> findByRemetenteIdAndLidaFalse(Long remetenteId);
    Long countByRemetenteIdAndLidaFalse(Long remetenteId);
    List<Mensagem> findByAtendimentoId(Long atendimentoId);
    List<Mensagem> findByAtendimentoIdAndRemetenteId(Long atendimentoId, Long remetenteId);
    List<Mensagem> findByAtendimentoIdAndDataEnvioBetween(Long atendimentoId, LocalDateTime inicio, LocalDateTime fim);
} 