package br.com.medicadebolso.domain.dto;

import br.com.medicadebolso.domain.model.StatusAtendimento;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class AtendimentoResponseDTO {
    private Long id;
    private Long medicoId;
    private String medicoNome;
    private Long pacienteId;
    private String pacienteNome;
    private StatusAtendimento status;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
    private String motivo;
    private String observacoes;
    private List<MensagemDTO> mensagens;
} 