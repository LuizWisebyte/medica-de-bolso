package br.com.medicadebolso.domain.exception;

public class AtendimentoNaoEncontradoException extends RuntimeException {
    public AtendimentoNaoEncontradoException(Long atendimentoId) {
        super("Atendimento não encontrado com ID: " + atendimentoId);
    }
} 