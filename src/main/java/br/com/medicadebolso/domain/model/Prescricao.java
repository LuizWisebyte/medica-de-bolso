package br.com.medicadebolso.domain.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "prescricoes")
public class Prescricao {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "consulta_id", nullable = false)
    private Consulta consulta;
    
    @ManyToOne
    @JoinColumn(name = "medico_id", nullable = false)
    private Medico medico;
    
    @Column(nullable = false)
    private LocalDateTime dataPrescricao;
    
    @Column(length = 1000)
    private String observacoes;
    
    @OneToMany(mappedBy = "prescricao", cascade = CascadeType.ALL)
    private List<ItemPrescricao> itens;
    
    @PrePersist
    public void prePersist() {
        this.dataPrescricao = LocalDateTime.now();
    }
} 