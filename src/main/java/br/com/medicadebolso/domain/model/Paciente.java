package br.com.medicadebolso.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "pacientes")
@EqualsAndHashCode(callSuper = true)
public class Paciente extends Usuario {
    
    private String alergias;
    private String medicamentosEmUso;
    private String historicoMedico;
    private String planoDeSaude;
    private String numeroCarteirinha;
    
    public Paciente() {
        setTipoUsuario(TipoUsuario.PACIENTE);
    }
} 