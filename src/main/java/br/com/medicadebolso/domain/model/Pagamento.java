package br.com.medicadebolso.domain.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "pagamentos")
public class Pagamento {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "consulta_id", nullable = false)
    private Consulta consulta;
    
    @Column(nullable = false)
    private BigDecimal valor;
    
    @Column(nullable = false)
    private LocalDateTime dataPagamento;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusPagamento status;
    
    @Column(length = 100)
    private String metodoPagamento;
    
    @Column(length = 100)
    private String codigoTransacao;
    
    @Column(length = 500)
    private String observacoes;
    
    public enum StatusPagamento {
        PENDENTE,
        PROCESSANDO,
        CONCLUIDO,
        CANCELADO,
        REEMBOLSADO
    }
    
    @PrePersist
    public void prePersist() {
        this.dataPagamento = LocalDateTime.now();
        if (this.status == null) {
            this.status = StatusPagamento.PENDENTE;
        }
    }
} 