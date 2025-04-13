package br.com.medicadebolso.domain.repository;

import br.com.medicadebolso.domain.Mensagem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface MensagemRepository extends JpaRepository<Mensagem, UUID> {
    List<Mensagem> findByUsuarioId(UUID usuarioId);
    List<Mensagem> findByUsuarioIdAndLidaFalse(UUID usuarioId);
    Long countByUsuarioIdAndLidaFalse(UUID usuarioId);
    List<Mensagem> findByAtendimentoId(Long atendimentoId);
    List<Mensagem> findByAtendimentoIdAndUsuarioId(Long atendimentoId, UUID usuarioId);
    List<Mensagem> findByAtendimentoIdAndDataEnvioBetween(Long atendimentoId, LocalDateTime inicio, LocalDateTime fim);
} 