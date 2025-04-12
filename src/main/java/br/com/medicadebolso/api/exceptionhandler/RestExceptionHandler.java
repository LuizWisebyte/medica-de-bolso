package br.com.medicadebolso.api.exceptionhandler;

import br.com.medicadebolso.domain.exception.EntidadeNaoEncontradaException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Classe centralizada para tratamento de exceções da API REST.
 * Estende ResponseEntityExceptionHandler para já tratar exceções padrão do Spring MVC.
 */
@ControllerAdvice // Indica que esta classe interceptará exceções lançadas por Controllers.
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    // Nome padrão para o campo de detalhes do erro no JSON de resposta.
    private static final String DETALHE_ERRO_PADRAO = "Ocorreu um erro interno inesperado no sistema. Tente novamente e se o problema persistir, entre em contato com o administrador do sistema.";

    /**
     * Handler para exceções de Entidade Não Encontrada (e suas subclasses).
     * Retorna HTTP 404 Not Found.
     */
    @ExceptionHandler(EntidadeNaoEncontradaException.class)
    public ResponseEntity<Object> handleEntidadeNaoEncontrada(
            EntidadeNaoEncontradaException ex, WebRequest request) {
        
        HttpStatus status = HttpStatus.NOT_FOUND;
        ProblemType problemType = ProblemType.RECURSO_NAO_ENCONTRADO;
        String detail = ex.getMessage();
        
        Problem problem = createProblemBuilder(status, problemType, detail)
                .userMessage(detail) // Mensagem para o usuário final
                .build();
        
        return handleExceptionInternal(ex, problem, new HttpHeaders(), status, request);
    }

    /**
     * Handler para exceções de validação de DTOs (@Valid).
     * Captura MethodArgumentNotValidException.
     * Retorna HTTP 400 Bad Request.
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        ProblemType problemType = ProblemType.DADOS_INVALIDOS;
        String detail = "Um ou mais campos estão inválidos. Faça o preenchimento correto e tente novamente.";
        
        // Coleta todas as mensagens de erro de validação dos campos
        List<Problem.Object> problemObjects = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> Problem.Object.builder()
                        .name(fieldError.getField()) // Nome do campo com erro
                        .userMessage(fieldError.getDefaultMessage()) // Mensagem definida na anotação (@Size, @NotNull, etc)
                        .build())
                .collect(Collectors.toList());
        
        Problem problem = createProblemBuilder(status, problemType, detail)
                .userMessage(detail)
                .objects(problemObjects) // Adiciona a lista de erros por campo
                .build();
        
        return handleExceptionInternal(ex, problem, headers, status, request);
    }

    /**
     * Handler genérico para qualquer outra exceção não tratada especificamente.
     * Retorna HTTP 500 Internal Server Error.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleUncaught(Exception ex, WebRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ProblemType problemType = ProblemType.ERRO_DE_SISTEMA;
        String detail = DETALHE_ERRO_PADRAO;

        // Logar a exceção original é crucial para debug no backend!
        logger.error(ex.getMessage(), ex);
        
        Problem problem = createProblemBuilder(status, problemType, detail)
                .userMessage(detail)
                .build();

        return handleExceptionInternal(ex, problem, new HttpHeaders(), status, request);
    }

    /**
     * Método auxiliar para padronizar a criação do corpo da resposta de erro (Objeto Problem).
     * Sobrescrito de ResponseEntityExceptionHandler.
     */
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex, Object body, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        
        // Se o corpo da resposta ainda não for um objeto Problem, cria um básico.
        if (body == null) {
            body = Problem.builder()
                    .timestamp(OffsetDateTime.now())
                    .title(HttpStatus.valueOf(status.value()).getReasonPhrase()) // Título padrão baseado no status HTTP
                    .status(status.value())
                    .userMessage(DETALHE_ERRO_PADRAO) // Mensagem genérica para o usuário
                    .build();
        } else if (body instanceof String) {
            // Se for uma string, usa como título do Problem.
            body = Problem.builder()
                    .timestamp(OffsetDateTime.now())
                    .title((String) body)
                    .status(status.value())
                    .userMessage(DETALHE_ERRO_PADRAO)
                    .build();
        }
        
        return super.handleExceptionInternal(ex, body, headers, status, request);
    }

    /**
     * Cria um builder para o objeto Problem, preenchendo campos comuns.
     */
    private Problem.ProblemBuilder createProblemBuilder(HttpStatusCode status, ProblemType problemType, String detail) {
        return Problem.builder()
                .timestamp(OffsetDateTime.now())
                .status(status.value())
                .type(problemType.getUri()) // URI que identifica o tipo do problema
                .title(problemType.getTitle()) // Título legível para o tipo do problema
                .detail(detail); // Descrição técnica do erro específico
    }
} 