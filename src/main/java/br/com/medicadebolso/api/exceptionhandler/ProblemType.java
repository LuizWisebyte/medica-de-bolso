package br.com.medicadebolso.api.exceptionhandler;

import lombok.Getter;

/**
 * Enumeração dos tipos de problemas/erros que a API pode retornar,
 * mapeando para um título legível e um URI identificador.
 */
@Getter
public enum ProblemType {

    DADOS_INVALIDOS("/dados-invalidos", "Dados inválidos"),
    ERRO_DE_SISTEMA("/erro-de-sistema", "Erro de sistema"),
    PARAMETRO_INVALIDO("/parametro-invalido", "Parâmetro inválido"),
    MENSAGEM_INCOMPREENSIVEL("/mensagem-incompreensivel", "Mensagem incompreensível"),
    RECURSO_NAO_ENCONTRADO("/recurso-nao-encontrado", "Recurso não encontrado"),
    ENTIDADE_EM_USO("/entidade-em-uso", "Entidade em uso"),
    ERRO_NEGOCIO("/erro-negocio", "Violação de regra de negócio");

    private String title;
    private String uri;

    ProblemType(String path, String title) {
        // Define um URI base para os tipos de problema. Poderia ser a URL da documentação.
        this.uri = "https://medicadebolso.com.br/erros" + path;
        this.title = title;
    }

} 