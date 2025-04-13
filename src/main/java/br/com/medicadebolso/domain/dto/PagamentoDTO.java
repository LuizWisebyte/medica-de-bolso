package br.com.medicadebolso.domain.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class PagamentoDTO {
    private Long consultaId;
    private BigDecimal valor;
    private String metodoPagamento;
    private String observacoes;
} 