package br.com.medicadebolso.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank; // Adicionar validações básicas
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
@Table(name = "dados_bancarios")
public class DadosBancarios {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String razaoSocial;

    @NotBlank
    @Size(min = 14, max = 14) // CNPJ tem 14 dígitos
    @Column(nullable = false, unique = true, length = 14)
    private String cnpj; // Armazenar apenas números

    @NotBlank
    @Column(nullable = false, length = 3) // Código do banco geralmente tem 3 dígitos
    private String codigoBanco;

    @NotBlank
    @Column(nullable = false)
    private String nomeBanco; // Nome do banco para exibição

    @NotBlank
    @Column(nullable = false, length = 4) // Agência sem DV
    private String agencia;

    @Column(length = 1) // Dígito verificador da agência (opcional)
    private String agenciaDv;

    @NotBlank
    @Column(nullable = false, length = 12) // Número da conta sem DV (ajustar tamanho se necessário)
    private String conta;

    @NotBlank
    @Column(nullable = false, length = 1) // Dígito verificador da conta
    private String contaDv;

    @NotNull // Tipo de conta é obrigatório
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoConta tipoConta;

    // Enum para Tipo de Conta
    public enum TipoConta {
        CONTA_CORRENTE,
        CONTA_POUPANCA
        // Adicionar outros tipos se necessário
    }

    // Relacionamento inverso (opcional)
    // @OneToOne(mappedBy = "dadosBancarios")
    // private Medico medico;
} 