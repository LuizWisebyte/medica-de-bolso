package br.com.medicadebolso.domain.repository;

import br.com.medicadebolso.domain.model.MensagemChat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MensagemChatRepository extends JpaRepository<MensagemChat, Long> {
    
    List<MensagemChat> findByConsultaIdOrderByDataHoraAsc(Long consultaId);
    
    List<MensagemChat> findByConsultaIdAndLidaFalse(Long consultaId);
    
    long countByConsultaIdAndLidaFalse(Long consultaId);
} 