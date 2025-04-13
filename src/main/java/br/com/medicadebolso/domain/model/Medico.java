package br.com.medicadebolso.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "medicos")
@EqualsAndHashCode(callSuper = true)
public class Medico extends Usuario {
    
    @Column(nullable = false)
    private String crmNumero;
    
    @Column(nullable = false, length = 2)
    private String crmEstado;
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "medico_especialidades", joinColumns = @JoinColumn(name = "medico_id"))
    @Column(name = "especialidade")
    private Set<String> especialidades = new HashSet<>();
    
    private String curriculo;
    private String formacao;
    private String experiencia;
    private Double valorConsulta;
    private boolean disponivel = true;
    
    private String profilePicture;
    private Double rating;
    
    @JsonIgnore
    @Column(length = 1024)
    private String certificateToken;
    
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "endereco_id", referencedColumnName = "id")
    private Endereco endereco;
    
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "dados_bancarios_id", referencedColumnName = "id")
    private DadosBancarios dadosBancarios;
    
    public Medico() {
        setTipoUsuario(TipoUsuario.MEDICO);
    }
} 