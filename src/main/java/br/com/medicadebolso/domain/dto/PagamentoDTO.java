package br.com.medicadebolso.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PagamentoDTO {
    
    @NotNull(message = "O ID da consulta é obrigatório")
    private Long consultaId;
    
    @NotNull(message = "O valor é obrigatório")
    @Positive(message = "O valor deve ser positivo")
    private BigDecimal valor;
    
    @NotBlank(message = "O método de pagamento é obrigatório")
    @Size(max = 100, message = "O método de pagamento não pode ter mais de 100 caracteres")
    private String metodoPagamento;
    
    @Size(max = 500, message = "As observações não podem ter mais de 500 caracteres")
    private String observacoes;
} 