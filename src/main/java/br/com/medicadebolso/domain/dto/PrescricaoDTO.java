package br.com.medicadebolso.domain.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class PrescricaoDTO {
    
    @NotNull(message = "O ID da consulta é obrigatório")
    private Long consultaId;
    
    @NotNull(message = "O ID do médico é obrigatório")
    private Long medicoId;
    
    @Size(max = 1000, message = "As observações não podem ter mais de 1000 caracteres")
    private String observacoes;
    
    @Valid
    @NotNull(message = "A lista de itens é obrigatória")
    @Size(min = 1, message = "A prescrição deve ter pelo menos um item")
    private List<ItemPrescricaoDTO> itens;
} 