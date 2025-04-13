package br.com.medicadebolso.domain.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "resgates")
public class Resgate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medico_id", nullable = false)
    private Medico medico;

    @Column(nullable = false)
    private BigDecimal valorTotal;

    @Column(nullable = false)
    private LocalDateTime dataSolicitacao;

    @Column
    private LocalDateTime dataProcessamento; // Data em que o resgate foi efetivamente processado

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusResgate status;

    @Column(length = 200) // Para armazenar ID da transação do banco ou motivo da falha
    private String detalhesTransacao;

    // Relacionamento inverso: Um resgate agrupa vários pagamentos
    @OneToMany(mappedBy = "resgate", cascade = CascadeType.PERSIST) // Apenas PERSIST, não queremos deletar pagamentos se resgate falhar
    private List<Pagamento> pagamentosIncluidos = new ArrayList<>();

    public enum StatusResgate {
        PENDENTE, // Solicitação criada, aguardando processamento
        PROCESSANDO, // Transferência bancária em andamento
        CONCLUIDO, // Dinheiro transferido com sucesso
        FALHOU // Falha na transferência
    }

    @PrePersist
    protected void onCreate() {
        dataSolicitacao = LocalDateTime.now();
        status = StatusResgate.PENDENTE;
    }
} 