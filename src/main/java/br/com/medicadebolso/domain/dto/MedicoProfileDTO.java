package br.com.medicadebolso.domain.dto;

import jakarta.validation.Valid; // Para validar DTOs aninhados
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class MedicoProfileDTO {

    private Long id; // ID do usuário/médico

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    private String email;

    @NotBlank(message = "Telefone é obrigatório")
    private String telefone;

    @NotBlank(message = "Número do CRM é obrigatório")
    private String crmNumero;

    @NotBlank(message = "Estado do CRM é obrigatório")
    @Size(min = 2, max = 2, message = "UF do CRM deve ter 2 caracteres")
    private String crmEstado;

    private String profilePicture; // URL da foto

    private Double rating; // Média de avaliações

    @NotNull(message = "Especialidades não podem ser nulas")
    private Set<String> especialidades;

    private Double valorConsulta;

    // DTOs aninhados
    @Valid // Garante que as validações dentro de EnderecoDTO sejam aplicadas
    private EnderecoDTO endereco;

    @Valid // Garante que as validações dentro de DadosBancariosDTO sejam aplicadas
    private DadosBancariosDTO dadosBancarios;

    // Outros campos relevantes podem ser adicionados aqui (curriculo, formacao, etc.)
    // se forem necessários na visualização do perfil.
} 