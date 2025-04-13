package br.com.medicadebolso.domain.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "enderecos")
public class Endereco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 9) // Formato 00000-000
    private String cep;

    @Column(nullable = false)
    private String logradouro; // Corresponde a "Endereço" na tela

    @Column(nullable = false, length = 20) // Tamanho razoável para número
    private String numero;

    @Column(length = 100) // Complemento pode ser maior
    private String complemento;

    @Column(nullable = false)
    private String bairro;

    @Column // Ponto de referência pode ser opcional
    private String pontoReferencia;

    @Column(nullable = false)
    private String cidade;

    @Column(nullable = false, length = 2) // Adicionar estado (UF)
    private String uf;

    // Relacionamento inverso (opcional, mas útil se precisarmos navegar de Endereco para Medico)
    // @OneToOne(mappedBy = "endereco")
    // private Medico medico;
} 