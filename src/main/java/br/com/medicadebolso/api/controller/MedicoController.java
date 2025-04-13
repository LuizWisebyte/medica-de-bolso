package br.com.medicadebolso.api.controller;

import br.com.medicadebolso.domain.dto.MedicoProfileDTO;
import br.com.medicadebolso.domain.model.Usuario; // Para obter o usuário autenticado
import br.com.medicadebolso.domain.service.MedicoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication; // Para obter o usuário logado
import org.springframework.security.core.context.SecurityContextHolder; // Para obter o contexto de segurança
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/medicos") // Base path para endpoints de médico
public class MedicoController {

    private final MedicoService medicoService;

    @Autowired
    public MedicoController(MedicoService medicoService) {
        this.medicoService = medicoService;
    }

    // Endpoint para buscar o perfil do médico logado
    @GetMapping("/me")
    public ResponseEntity<MedicoProfileDTO> getMyProfile() {
        Long medicoId = getAuthenticatedUserId();
        MedicoProfileDTO profile = medicoService.getMedicoProfile(medicoId);
        return ResponseEntity.ok(profile);
    }

    // Endpoint para atualizar o perfil do médico logado
    @PutMapping("/me")
    public ResponseEntity<MedicoProfileDTO> updateMyProfile(@Valid @RequestBody MedicoProfileDTO medicoProfileDTO) {
        Long medicoId = getAuthenticatedUserId();
        // Idealmente, garantir que o ID no DTO (se existir) corresponda ao logado ou seja ignorado
        MedicoProfileDTO updatedProfile = medicoService.updateMedicoProfile(medicoId, medicoProfileDTO);
        return ResponseEntity.ok(updatedProfile);
    }

    // Método auxiliar para obter o ID do usuário autenticado
    private Long getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Usuario) {
            Usuario usuario = (Usuario) authentication.getPrincipal();
            // Aqui estamos assumindo que o ID do Medico é o mesmo ID do Usuario base.
            // Se houver uma tabela Medico separada com seu próprio ID, pode ser necessário
            // buscar o Medico pelo Usuario ID.
            return usuario.getId(); 
        }
        // Lançar uma exceção ou tratar caso não autenticado apropriadamente
        throw new IllegalStateException("Usuário não autenticado."); 
    }
} 