package br.com.medicadebolso.domain.dto;

import br.com.medicadebolso.domain.model.Resgate;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ResgateResponseDTO {
    private Long id;
    private BigDecimal valor;
    private Resgate.StatusResgate status;
    private LocalDateTime dataSolicitacao;
    private LocalDateTime dataProcessamento;
    private String mensagem;
} 