package br.com.medicadebolso.domain.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class SolicitarResgateDTO {
    @NotNull(message = "O valor do resgate é obrigatório")
    @Positive(message = "O valor do resgate deve ser maior que zero")
    private BigDecimal valor;

    @NotNull(message = "O ID do médico é obrigatório")
    private Long medicoId;
} 