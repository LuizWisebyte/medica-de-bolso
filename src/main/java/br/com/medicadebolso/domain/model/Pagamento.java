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
    private BigDecimal valorBruto;
    
    @Column(nullable = false)
    private BigDecimal valorLiquido;
    
    @Column(nullable = false)
    private String metodoPagamento;
    
    @Column
    private String observacoes;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusPagamento status;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusDisponibilidade statusDisponibilidade;
    
    @Column(nullable = false, unique = true)
    private String codigoTransacao;
    
    @Column(nullable = false)
    private LocalDateTime dataPagamento;
    
    @ManyToOne
    @JoinColumn(name = "resgate_id")
    private Resgate resgate;
    
    public enum StatusPagamento {
        PROCESSANDO,
        CONCLUIDO,
        CANCELADO,
        REJEITADO
    }
    
    public enum StatusDisponibilidade {
        DISPONIVEL,
        RESGATADO,
        BLOQUEADO
    }
    
    @PrePersist
    protected void onCreate() {
        dataPagamento = LocalDateTime.now();
        statusDisponibilidade = StatusDisponibilidade.DISPONIVEL;
        valorBruto = valor;
        valorLiquido = valor.multiply(BigDecimal.valueOf(0.95)); // 5% de taxa
    }
} 