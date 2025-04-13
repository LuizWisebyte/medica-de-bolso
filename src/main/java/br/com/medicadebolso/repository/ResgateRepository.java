package br.com.medicadebolso.repository;

import br.com.medicadebolso.domain.Resgate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ResgateRepository extends JpaRepository<Resgate, Long> {
    List<Resgate> findByUsuarioIdOrderByDataSolicitacaoDesc(Long usuarioId);
} 