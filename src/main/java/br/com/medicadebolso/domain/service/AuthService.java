package br.com.medicadebolso.domain.service;

import br.com.medicadebolso.domain.dto.LoginDTO;
import br.com.medicadebolso.domain.dto.RegistroDTO;
import br.com.medicadebolso.domain.model.Medico;
import br.com.medicadebolso.domain.model.Paciente;
import br.com.medicadebolso.domain.model.Usuario;
import br.com.medicadebolso.domain.model.Usuario.TipoUsuario;
import br.com.medicadebolso.domain.repository.UsuarioRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    
    public AuthService(UsuarioRepository usuarioRepository,
                      PasswordEncoder passwordEncoder,
                      AuthenticationManager authenticationManager,
                      JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }
    
    @Transactional
    public String registrar(RegistroDTO registroDTO) {
        if (usuarioRepository.existsByEmail(registroDTO.getEmail())) {
            throw new RuntimeException("Email já cadastrado");
        }
        
        if (usuarioRepository.existsByCpf(registroDTO.getCpf())) {
            throw new RuntimeException("CPF já cadastrado");
        }
        
        Usuario usuario;
        if (registroDTO.getTipoUsuario() == TipoUsuario.PACIENTE) {
            usuario = criarPaciente(registroDTO);
        } else {
            usuario = criarMedico(registroDTO);
        }
        
        usuario.setSenha(passwordEncoder.encode(registroDTO.getSenha()));
        usuarioRepository.save(usuario);
        
        return jwtService.gerarToken(usuario);
    }
    
    public String login(LoginDTO loginDTO) {
        var authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getSenha())
        );
        
        var usuario = (Usuario) authentication.getPrincipal();
        return jwtService.gerarToken(usuario);
    }
    
    private Paciente criarPaciente(RegistroDTO registroDTO) {
        var paciente = new Paciente();
        paciente.setNome(registroDTO.getNome());
        paciente.setEmail(registroDTO.getEmail());
        paciente.setCpf(registroDTO.getCpf());
        paciente.setTelefone(registroDTO.getTelefone());
        paciente.setAlergias(registroDTO.getAlergias());
        paciente.setMedicamentosEmUso(registroDTO.getMedicamentosEmUso());
        paciente.setHistoricoMedico(registroDTO.getHistoricoMedico());
        paciente.setPlanoDeSaude(registroDTO.getPlanoDeSaude());
        paciente.setNumeroCarteirinha(registroDTO.getNumeroCarteirinha());
        return paciente;
    }
    
    private Medico criarMedico(RegistroDTO registroDTO) {
        var medico = new Medico();
        medico.setNome(registroDTO.getNome());
        medico.setEmail(registroDTO.getEmail());
        medico.setCpf(registroDTO.getCpf());
        medico.setTelefone(registroDTO.getTelefone());
        medico.setCrmNumero(registroDTO.getCrm());
        medico.setCrmEstado(registroDTO.getCrmEstado());
        medico.setEspecialidades(registroDTO.getEspecialidades());
        medico.setCurriculo(registroDTO.getCurriculo());
        medico.setFormacao(registroDTO.getFormacao());
        medico.setExperiencia(registroDTO.getExperiencia());
        medico.setValorConsulta(registroDTO.getValorConsulta());
        return medico;
    }
} 