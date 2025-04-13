package br.com.medicadebolso.domain.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "atendimentos")
public class Atendimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medico_id", nullable = false)
    private Medico medico;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusAtendimento status;

    @Column(nullable = false)
    private LocalDateTime dataCriacao;

    @Column
    private LocalDateTime dataInicio;

    @Column
    private LocalDateTime dataFim;

    @Column(length = 500)
    private String motivo;

    @Column(length = 1000)
    private String observacoes;

    @OneToMany(mappedBy = "atendimento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Mensagem> mensagens = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        dataCriacao = LocalDateTime.now();
        status = StatusAtendimento.AGUARDANDO_MEDICO;
    }

    public void iniciar() {
        if (status == StatusAtendimento.AGUARDANDO_MEDICO) {
            status = StatusAtendimento.EM_ANDAMENTO;
            dataInicio = LocalDateTime.now();
        }
    }

    public void finalizar() {
        if (status == StatusAtendimento.EM_ANDAMENTO) {
            status = StatusAtendimento.FINALIZADO;
            dataFim = LocalDateTime.now();
        }
    }

    public void cancelar() {
        if (status != StatusAtendimento.FINALIZADO && status != StatusAtendimento.CANCELADO) {
            status = StatusAtendimento.CANCELADO;
            dataFim = LocalDateTime.now();
        }
    }

    public void expirar() {
        if (status == StatusAtendimento.AGUARDANDO_MEDICO) {
            status = StatusAtendimento.EXPIRADO;
            dataFim = LocalDateTime.now();
        }
    }
} 