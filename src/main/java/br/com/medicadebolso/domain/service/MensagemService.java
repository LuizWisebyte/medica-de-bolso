package br.com.medicadebolso.domain.service;

import br.com.medicadebolso.domain.model.Mensagem;
import br.com.medicadebolso.domain.dto.MensagemDTO;
import br.com.medicadebolso.domain.model.Usuario;
import br.com.medicadebolso.domain.model.Atendimento;
import br.com.medicadebolso.domain.repository.MensagemRepository;
import br.com.medicadebolso.domain.repository.UsuarioRepository;
import br.com.medicadebolso.domain.repository.AtendimentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityNotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MensagemService {

    @Autowired
    private MensagemRepository mensagemRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private AtendimentoRepository atendimentoRepository;

    @Transactional
    public MensagemDTO enviarMensagem(MensagemDTO mensagemDTO) {
        Usuario remetente = usuarioRepository.findById(mensagemDTO.getUsuarioId())
            .orElseThrow(() -> new EntityNotFoundException("Remetente não encontrado com ID: " + mensagemDTO.getUsuarioId()));
        
        Atendimento atendimento = null;
        if (mensagemDTO.getAtendimentoId() != null) {
            atendimento = atendimentoRepository.findById(mensagemDTO.getAtendimentoId())
                .orElseThrow(() -> new EntityNotFoundException("Atendimento não encontrado com ID: " + mensagemDTO.getAtendimentoId()));
        }

        Mensagem mensagem = new Mensagem();
        mensagem.setRemetente(remetente);
        mensagem.setAtendimento(atendimento);
        mensagem.setConteudo(mensagemDTO.getConteudo());
        
        mensagem = mensagemRepository.save(mensagem);
        return convertToDTO(mensagem);
    }

    private MensagemDTO convertToDTO(Mensagem mensagem) {
        MensagemDTO dto = new MensagemDTO();
        dto.setId(mensagem.getId());
        if (mensagem.getRemetente() != null) {
            dto.setUsuarioId(mensagem.getRemetente().getId());
        }
        if (mensagem.getAtendimento() != null) {
             dto.setAtendimentoId(mensagem.getAtendimento().getId());
        }
        dto.setConteudo(mensagem.getConteudo());
        dto.setLida(mensagem.isLida());
        dto.setDataEnvio(mensagem.getDataEnvio());
        return dto;
    }

    public List<Mensagem> buscarMensagensPorRemetente(Long remetenteId) {
        return mensagemRepository.findByRemetenteId(remetenteId);
    }

    public List<Mensagem> buscarMensagensNaoLidasPorRemetente(Long remetenteId) {
        return mensagemRepository.findByRemetenteIdAndLidaFalse(remetenteId);
    }

    public Long contarMensagensNaoLidasPorRemetente(Long remetenteId) {
        return mensagemRepository.countByRemetenteIdAndLidaFalse(remetenteId);
    }

    @Transactional
    public void marcarComoLida(Long mensagemId) {
        mensagemRepository.findById(mensagemId).ifPresent(mensagem -> {
            mensagem.setLida(true);
            mensagemRepository.save(mensagem);
        });
    }

    @Transactional(readOnly = true)
    public List<MensagemDTO> buscarMensagensPorAtendimento(Long atendimentoId) {
        List<Mensagem> mensagens = mensagemRepository.findByAtendimentoId(atendimentoId);
        return mensagens.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void marcarMensagensComoLidas(Long atendimentoId, Long remetenteId) {
        List<Mensagem> mensagens = mensagemRepository.findByAtendimentoIdAndRemetenteId(atendimentoId, remetenteId);
        mensagens.forEach(mensagem -> {
            mensagem.setLida(true);
            mensagemRepository.save(mensagem);
        });
    }

    @Transactional(readOnly = true)
    public List<MensagemDTO> buscarMensagensPorPeriodo(Long atendimentoId, LocalDateTime inicio, LocalDateTime fim) {
        List<Mensagem> mensagens = mensagemRepository.findByAtendimentoIdAndDataEnvioBetween(atendimentoId, inicio, fim);
        return mensagens.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
} 