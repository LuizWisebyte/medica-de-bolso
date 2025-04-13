package br.com.medicadebolso.domain.dto;

import br.com.medicadebolso.domain.model.DadosBancarios.TipoConta; // Importar o Enum
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class DadosBancariosDTO {

    @NotBlank(message = "Razão social é obrigatória")
    private String razaoSocial;

    @NotBlank(message = "CNPJ é obrigatório")
    @Pattern(regexp = "\\d{14}", message = "CNPJ deve conter 14 números")
    private String cnpj; // Receber apenas números

    @NotBlank(message = "Código do banco é obrigatório")
    @Pattern(regexp = "\\d{3}", message = "Código do banco deve ter 3 números")
    private String codigoBanco;

    @NotBlank(message = "Nome do banco é obrigatório")
    private String nomeBanco;

    @NotBlank(message = "Agência é obrigatória")
    @Pattern(regexp = "\\d{1,4}", message = "Agência deve ter até 4 números") // Ajustar regex se necessário
    private String agencia;

    @Pattern(regexp = "\\d{0,1}", message = "Dígito verificador da agência deve ter 1 número")
    private String agenciaDv; // Opcional

    @NotBlank(message = "Número da conta é obrigatório")
     @Pattern(regexp = "\\d{1,12}", message = "Número da conta deve ter até 12 números") // Ajustar regex se necessário
    private String conta;

    @NotBlank(message = "Dígito verificador da conta é obrigatório")
    @Pattern(regexp = "\\d{1}", message = "Dígito verificador da conta deve ter 1 número")
    private String contaDv;

    @NotNull(message = "Tipo de conta é obrigatório")
    private TipoConta tipoConta;
} 