package br.com.medicadebolso.api.exceptionhandler;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Representa o corpo da resposta para erros da API, seguindo RFC 7807 (Problem Details).
 */
@JsonInclude(JsonInclude.Include.NON_NULL) // Não inclui campos nulos no JSON final
@Getter
@Builder // Usa o padrão Builder para facilitar a construção do objeto
public class Problem {

    // HTTP Status Code
    private Integer status;

    // URI que identifica o tipo do problema (ex: /erros/recurso-nao-encontrado)
    private String type;

    // Título curto e legível para o tipo do problema (ex: Recurso não encontrado)
    private String title;

    // Descrição detalhada e técnica do erro específico que ocorreu.
    private String detail;

    // Mensagem voltada para o usuário final da API (pode ser igual ao detail ou mais amigável).
    private String userMessage;

    // Timestamp de quando o erro ocorreu.
    private OffsetDateTime timestamp;

    // Lista de objetos ou campos que causaram o erro (útil para erros de validação).
    private List<Object> objects;

    /**
     * Subclasse para representar detalhes de campos inválidos.
     */
    @Getter
    @Builder
    public static class Object {
        private String name; // Nome do campo/propriedade inválida
        private String userMessage; // Mensagem específica para o campo inválido
    }
} 