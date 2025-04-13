package br.com.medicadebolso.domain.dto;

import br.com.medicadebolso.domain.model.Usuario.TipoUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.Set;
import java.util.HashSet;

@Data
public class RegistroDTO {
    
    @NotBlank(message = "Nome é obrigatório")
    private String nome;
    
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    private String email;
    
    @NotBlank(message = "Senha é obrigatória")
    private String senha;
    
    @NotBlank(message = "CPF é obrigatório")
    private String cpf;
    
    @NotBlank(message = "Telefone é obrigatório")
    private String telefone;
    
    @NotNull(message = "Tipo de usuário é obrigatório")
    private TipoUsuario tipoUsuario;
    
    // Campos específicos para Paciente
    private String alergias;
    private String medicamentosEmUso;
    private String historicoMedico;
    private String planoDeSaude;
    private String numeroCarteirinha;
    
    // Campos específicos para Médico
    private String crm;
    private String crmEstado;
    private Set<String> especialidades = new HashSet<>();
    private String curriculo;
    private String formacao;
    private String experiencia;
    private Double valorConsulta;
} 