package br.com.medicadebolso.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MensagemChatDTO {
    
    @NotNull(message = "O ID da consulta é obrigatório")
    private Long consultaId;
    
    @NotNull(message = "O ID do remetente é obrigatório")
    private Long remetenteId;
    
    @NotBlank(message = "O conteúdo da mensagem é obrigatório")
    private String conteudo;
} 