package br.com.medicadebolso.domain;

import br.com.medicadebolso.domain.enums.StatusResgate;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "resgates")
public class Resgate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long usuarioId;

    @Column(nullable = false)
    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusResgate status;

    @Column
    private String motivoRejeicao;

    @Column(nullable = false)
    private LocalDateTime dataSolicitacao;

    @Column
    private LocalDateTime dataProcessamento;

    @Column
    private LocalDateTime dataConclusao;
} 