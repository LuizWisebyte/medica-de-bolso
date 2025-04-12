package br.com.medicadebolso.domain.service;

import br.com.medicadebolso.domain.dto.MensagemChatDTO;
import br.com.medicadebolso.domain.model.Consulta;
import br.com.medicadebolso.domain.model.MensagemChat;
import br.com.medicadebolso.domain.model.Usuario;
import br.com.medicadebolso.domain.repository.ConsultaRepository;
import br.com.medicadebolso.domain.repository.MensagemChatRepository;
import br.com.medicadebolso.domain.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ChatService {
    
    @Autowired
    private MensagemChatRepository mensagemChatRepository;
    
    @Autowired
    private ConsultaRepository consultaRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    @Transactional
    public MensagemChat enviarMensagem(MensagemChatDTO mensagemDTO) {
        Consulta consulta = consultaRepository.findById(mensagemDTO.getConsultaId())
                .orElseThrow(() -> new RuntimeException("Consulta não encontrada"));
                
        Usuario remetente = usuarioRepository.findById(mensagemDTO.getRemetenteId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        MensagemChat mensagem = new MensagemChat();
        mensagem.setConsulta(consulta);
        mensagem.setRemetente(remetente);
        mensagem.setConteudo(mensagemDTO.getConteudo());
        
        mensagem = mensagemChatRepository.save(mensagem);
        
        // Envia a mensagem para o tópico WebSocket
        messagingTemplate.convertAndSend("/topic/chat/" + consulta.getId(), mensagem);
        
        return mensagem;
    }
    
    public List<MensagemChat> buscarMensagens(Long consultaId) {
        return mensagemChatRepository.findByConsultaIdOrderByDataHoraAsc(consultaId);
    }
    
    @Transactional
    public void marcarComoLida(Long mensagemId) {
        MensagemChat mensagem = mensagemChatRepository.findById(mensagemId)
                .orElseThrow(() -> new RuntimeException("Mensagem não encontrada"));
                
        mensagem.setLida(true);
        mensagemChatRepository.save(mensagem);
    }
    
    public long contarMensagensNaoLidas(Long consultaId) {
        return mensagemChatRepository.countByConsultaIdAndLidaFalse(consultaId);
    }
} 