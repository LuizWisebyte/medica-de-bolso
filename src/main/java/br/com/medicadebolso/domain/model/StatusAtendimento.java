package br.com.medicadebolso.domain.model;

public enum StatusAtendimento {
    AGUARDANDO_MEDICO,    // Atendimento criado, aguardando médico aceitar
    EM_ANDAMENTO,         // Atendimento em progresso
    FINALIZADO,           // Atendimento finalizado normalmente
    CANCELADO,            // Atendimento cancelado
    EXPIRADO              // Atendimento expirou por inatividade
} 