package br.com.medicadebolso.domain.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "itens_prescricao")
public class ItemPrescricao {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "prescricao_id", nullable = false)
    private Prescricao prescricao;
    
    @Column(nullable = false, length = 200)
    private String medicamento;
    
    @Column(nullable = false, length = 50)
    private String dosagem;
    
    @Column(nullable = false, length = 100)
    private String viaAdministracao;
    
    @Column(nullable = false, length = 100)
    private String frequencia;
    
    @Column(nullable = false)
    private int duracaoDias;
    
    @Column(length = 500)
    private String observacoes;
} 