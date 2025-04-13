package br.com.medicadebolso.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AtendimentoDTO {
    
    @NotNull(message = "O ID do médico é obrigatório")
    private Long medicoId;
    
    @NotNull(message = "O ID do paciente é obrigatório")
    private Long pacienteId;
    
    @NotBlank(message = "O motivo do atendimento é obrigatório")
    @Size(max = 500, message = "O motivo não pode ter mais de 500 caracteres")
    private String motivo;
    
    @Size(max = 1000, message = "As observações não podem ter mais de 1000 caracteres")
    private String observacoes;
} 