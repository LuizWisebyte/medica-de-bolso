package br.com.medicadebolso.domain.exception;

public class PacienteNaoEncontradoException extends RuntimeException {
    public PacienteNaoEncontradoException(Long pacienteId) {
        super("Paciente não encontrado com ID: " + pacienteId);
    }
} 