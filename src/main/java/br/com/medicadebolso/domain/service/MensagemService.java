package br.com.medicadebolso.domain.service;

import br.com.medicadebolso.domain.Mensagem;
import br.com.medicadebolso.domain.dto.MensagemDTO;
import br.com.medicadebolso.domain.repository.MensagemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MensagemService {

    @Autowired
    private MensagemRepository mensagemRepository;

    @Transactional
    public MensagemDTO enviarMensagem(MensagemDTO mensagemDTO) {
        Mensagem mensagem = new Mensagem();
        mensagem.setUsuarioId(mensagemDTO.getUsuarioId());
        mensagem.setTitulo(mensagemDTO.getTitulo());
        mensagem.setConteudo(mensagemDTO.getConteudo());
        mensagem.setDataEnvio(LocalDateTime.now());
        mensagem.setLida(false);
        
        mensagem = mensagemRepository.save(mensagem);
        return convertToDTO(mensagem);
    }

    private MensagemDTO convertToDTO(Mensagem mensagem) {
        MensagemDTO dto = new MensagemDTO();
        dto.setId(mensagem.getId());
        dto.setUsuarioId(mensagem.getUsuarioId());
        dto.setAtendimentoId(mensagem.getAtendimentoId());
        dto.setTitulo(mensagem.getTitulo());
        dto.setConteudo(mensagem.getConteudo());
        dto.setLida(mensagem.isLida());
        dto.setDataEnvio(mensagem.getDataEnvio());
        return dto;
    }

    public List<Mensagem> buscarMensagensPorUsuario(UUID usuarioId) {
        return mensagemRepository.findByUsuarioId(usuarioId);
    }

    public List<Mensagem> buscarMensagensNaoLidasPorUsuario(UUID usuarioId) {
        return mensagemRepository.findByUsuarioIdAndLidaFalse(usuarioId);
    }

    public Long contarMensagensNaoLidasPorUsuario(UUID usuarioId) {
        return mensagemRepository.countByUsuarioIdAndLidaFalse(usuarioId);
    }

    @Transactional
    public void marcarComoLida(UUID mensagemId) {
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
    public void marcarMensagensComoLidas(Long atendimentoId, Long usuarioId) {
        List<Mensagem> mensagens = mensagemRepository.findByAtendimentoIdAndUsuarioId(atendimentoId, UUID.fromString(usuarioId.toString()));
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