package br.com.medicadebolso.domain.model;

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
    
    @Column(nullable = false, unique = true)
    private String crm;
    
    @Column(nullable = false)
    private String especialidade;
    
    @ElementCollection
    @CollectionTable(name = "medico_especialidades", joinColumns = @JoinColumn(name = "medico_id"))
    @Column(name = "especialidade")
    private Set<String> especialidades = new HashSet<>();
    
    private String curriculo;
    private String formacao;
    private String experiencia;
    private Double valorConsulta;
    private boolean disponivel = true;
    
    public Medico() {
        setTipoUsuario(TipoUsuario.MEDICO);
    }
} 