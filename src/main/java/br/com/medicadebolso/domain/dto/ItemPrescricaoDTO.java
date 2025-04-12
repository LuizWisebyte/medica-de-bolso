package br.com.medicadebolso.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ItemPrescricaoDTO {
    
    @NotBlank(message = "O nome do medicamento é obrigatório")
    @Size(max = 200, message = "O nome do medicamento não pode ter mais de 200 caracteres")
    private String medicamento;
    
    @NotBlank(message = "A dosagem é obrigatória")
    @Size(max = 50, message = "A dosagem não pode ter mais de 50 caracteres")
    private String dosagem;
    
    @NotBlank(message = "A via de administração é obrigatória")
    @Size(max = 100, message = "A via de administração não pode ter mais de 100 caracteres")
    private String viaAdministracao;
    
    @NotBlank(message = "A frequência é obrigatória")
    @Size(max = 100, message = "A frequência não pode ter mais de 100 caracteres")
    private String frequencia;
    
    @NotNull(message = "A duração em dias é obrigatória")
    @Positive(message = "A duração deve ser um número positivo")
    private int duracaoDias;
    
    @Size(max = 500, message = "As observações não podem ter mais de 500 caracteres")
    private String observacoes;
} 