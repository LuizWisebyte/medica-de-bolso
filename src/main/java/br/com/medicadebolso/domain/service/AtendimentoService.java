package br.com.medicadebolso.domain.service;

import br.com.medicadebolso.domain.dto.AtendimentoDTO;
import br.com.medicadebolso.domain.dto.AtendimentoResponseDTO;
import br.com.medicadebolso.domain.dto.MensagemDTO;
import br.com.medicadebolso.domain.exception.MedicoNaoEncontradoException;
import br.com.medicadebolso.domain.exception.PacienteNaoEncontradoException;
import br.com.medicadebolso.domain.model.*;
import br.com.medicadebolso.domain.repository.AtendimentoRepository;
import br.com.medicadebolso.domain.repository.MedicoRepository;
import br.com.medicadebolso.domain.repository.MensagemRepository;
import br.com.medicadebolso.domain.repository.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AtendimentoService {

    @Autowired
    private AtendimentoRepository atendimentoRepository;

    @Autowired
    private MensagemRepository mensagemRepository;

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Transactional
    public AtendimentoResponseDTO criarAtendimento(AtendimentoDTO dto) {
        Medico medico = medicoRepository.findById(dto.getMedicoId())
                .orElseThrow(() -> new MedicoNaoEncontradoException(dto.getMedicoId()));

        Paciente paciente = pacienteRepository.findById(dto.getPacienteId())
                .orElseThrow(() -> new PacienteNaoEncontradoException(dto.getPacienteId()));

        Atendimento atendimento = new Atendimento();
        atendimento.setMedico(medico);
        atendimento.setPaciente(paciente);
        atendimento.setMotivo(dto.getMotivo());
        atendimento.setObservacoes(dto.getObservacoes());

        atendimento = atendimentoRepository.save(atendimento);
        return mapToAtendimentoResponseDTO(atendimento);
    }

    @Transactional
    public AtendimentoResponseDTO iniciarAtendimento(Long atendimentoId) {
        Atendimento atendimento = atendimentoRepository.findById(atendimentoId)
                .orElseThrow(() -> new RuntimeException("Atendimento não encontrado"));

        atendimento.iniciar();
        atendimento = atendimentoRepository.save(atendimento);
        return mapToAtendimentoResponseDTO(atendimento);
    }

    @Transactional
    public AtendimentoResponseDTO finalizarAtendimento(Long atendimentoId) {
        Atendimento atendimento = atendimentoRepository.findById(atendimentoId)
                .orElseThrow(() -> new RuntimeException("Atendimento não encontrado"));

        atendimento.finalizar();
        atendimento = atendimentoRepository.save(atendimento);
        return mapToAtendimentoResponseDTO(atendimento);
    }

    @Transactional
    public AtendimentoResponseDTO cancelarAtendimento(Long atendimentoId) {
        Atendimento atendimento = atendimentoRepository.findById(atendimentoId)
                .orElseThrow(() -> new RuntimeException("Atendimento não encontrado"));

        atendimento.cancelar();
        atendimento = atendimentoRepository.save(atendimento);
        return mapToAtendimentoResponseDTO(atendimento);
    }

    @Transactional(readOnly = true)
    public List<AtendimentoResponseDTO> listarAtendimentosPorMedico(Long medicoId) {
        return atendimentoRepository.findByMedicoIdAndStatus(medicoId, StatusAtendimento.EM_ANDAMENTO)
                .stream()
                .map(this::mapToAtendimentoResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AtendimentoResponseDTO> listarAtendimentosPorPaciente(Long pacienteId) {
        return atendimentoRepository.findByPacienteIdAndStatus(pacienteId, StatusAtendimento.EM_ANDAMENTO)
                .stream()
                .map(this::mapToAtendimentoResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AtendimentoResponseDTO buscarAtendimento(Long atendimentoId) {
        Atendimento atendimento = atendimentoRepository.findById(atendimentoId)
                .orElseThrow(() -> new RuntimeException("Atendimento não encontrado"));

        return mapToAtendimentoResponseDTO(atendimento);
    }

    @Transactional
    public void expirarAtendimentosInativos() {
        LocalDateTime dataLimite = LocalDateTime.now().minusHours(24);
        List<Atendimento> atendimentosExpirados = atendimentoRepository
                .findByStatusAndDataCriacaoBefore(StatusAtendimento.AGUARDANDO_MEDICO, dataLimite);

        for (Atendimento atendimento : atendimentosExpirados) {
            atendimento.expirar();
        }

        atendimentoRepository.saveAll(atendimentosExpirados);
    }

    private AtendimentoResponseDTO mapToAtendimentoResponseDTO(Atendimento atendimento) {
        AtendimentoResponseDTO dto = new AtendimentoResponseDTO();
        dto.setId(atendimento.getId());
        dto.setMedicoId(atendimento.getMedico().getId());
        dto.setMedicoNome(atendimento.getMedico().getNome());
        dto.setPacienteId(atendimento.getPaciente().getId());
        dto.setPacienteNome(atendimento.getPaciente().getNome());
        dto.setStatus(atendimento.getStatus());
        dto.setDataCriacao(atendimento.getDataCriacao());
        dto.setDataInicio(atendimento.getDataInicio());
        dto.setDataFim(atendimento.getDataFim());
        dto.setMotivo(atendimento.getMotivo());
        dto.setObservacoes(atendimento.getObservacoes());

        List<MensagemDTO> mensagensDTO = atendimento.getMensagens().stream()
                .map(this::mapToMensagemDTO)
                .collect(Collectors.toList());
        dto.setMensagens(mensagensDTO);

        return dto;
    }

    private MensagemDTO mapToMensagemDTO(Mensagem mensagem) {
        MensagemDTO dto = new MensagemDTO();
        dto.setAtendimentoId(mensagem.getAtendimento().getId());
        dto.setUsuarioId(mensagem.getRemetente().getId());
        dto.setConteudo(mensagem.getConteudo());
        dto.setDataEnvio(mensagem.getDataEnvio());
        dto.setLida(mensagem.isLida());
        return dto;
    }
} 