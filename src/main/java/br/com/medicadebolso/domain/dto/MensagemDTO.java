package br.com.medicadebolso.domain.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MensagemDTO {
    private Long id;
    private Long usuarioId;
    private Long atendimentoId;
    private String titulo;
    private String conteudo;
    private boolean lida;
    private LocalDateTime dataEnvio;
} 