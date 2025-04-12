package br.com.medicadebolso.domain.exception;

public class ConsultaNaoEncontradaException extends EntidadeNaoEncontradaException {

    private static final long serialVersionUID = 1L;

    public ConsultaNaoEncontradaException(String mensagem) {
        super(mensagem);
    }

    public ConsultaNaoEncontradaException(Long consultaId) {
        this(String.format("Não existe um cadastro de consulta com o código %d", consultaId));
    }

} 