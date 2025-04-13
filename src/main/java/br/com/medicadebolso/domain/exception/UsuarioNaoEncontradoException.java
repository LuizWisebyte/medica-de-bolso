package br.com.medicadebolso.domain.exception;

public class UsuarioNaoEncontradoException extends RuntimeException {
    public UsuarioNaoEncontradoException(Long usuarioId) {
        super("Usuário não encontrado com ID: " + usuarioId);
    }
} 