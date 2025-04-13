package br.com.medicadebolso.service;

import br.com.medicadebolso.api.dto.MensagemDTO;
import br.com.medicadebolso.domain.Mensagem;
import br.com.medicadebolso.repository.MensagemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
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
        mensagem.setLida(false);
        mensagem.setDataEnvio(LocalDateTime.now());

        mensagem = mensagemRepository.save(mensagem);
        return convertToDTO(mensagem);
    }

    @Transactional(readOnly = true)
    public List<MensagemDTO> buscarMensagensPorUsuario(Long usuarioId) {
        List<Mensagem> mensagens = mensagemRepository.findByUsuarioId(usuarioId);
        return mensagens.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void marcarComoLida(Long mensagemId) {
        Mensagem mensagem = mensagemRepository.findById(mensagemId)
                .orElseThrow(() -> new RuntimeException("Mensagem não encontrada"));
        mensagem.setLida(true);
        mensagemRepository.save(mensagem);
    }

    private MensagemDTO convertToDTO(Mensagem mensagem) {
        MensagemDTO dto = new MensagemDTO();
        dto.setId(mensagem.getId());
        dto.setUsuarioId(mensagem.getUsuarioId());
        dto.setTitulo(mensagem.getTitulo());
        dto.setConteudo(mensagem.getConteudo());
        dto.setLida(mensagem.isLida());
        dto.setDataEnvio(mensagem.getDataEnvio());
        return dto;
    }
} 