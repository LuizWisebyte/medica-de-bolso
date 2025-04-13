package br.com.medicadebolso.repository;

import br.com.medicadebolso.domain.Mensagem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MensagemRepository extends JpaRepository<Mensagem, Long> {
    List<Mensagem> findByUsuarioId(Long usuarioId);
} 