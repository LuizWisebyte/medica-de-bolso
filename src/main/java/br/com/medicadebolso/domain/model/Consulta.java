package br.com.medicadebolso.domain.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "consultas")
public class Consulta {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;
    
    @ManyToOne
    @JoinColumn(name = "medico_id", nullable = false)
    private Medico medico;
    
    @Column(nullable = false)
    private LocalDateTime dataHora;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusConsulta status;
    
    @Column(length = 1000)
    private String queixaInicial;
    
    @Column(length = 1000)
    private String diagnostico;
    
    @Column(length = 1000)
    private String prescricao;
    
    @Column(length = 1000)
    private String observacoes;
    
    private Double valor;
    
    @Column(nullable = false)
    private boolean pago = false;
    
    public enum StatusConsulta {
        AGENDADA,
        EM_ANDAMENTO,
        CONCLUIDA,
        CANCELADA,
        NAO_COMPARECEU
    }
} 