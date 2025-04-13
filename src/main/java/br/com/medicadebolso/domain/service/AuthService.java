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
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AuthService {
    
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    
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
        log.info("Tentativa de registro recebida para email: {}", registroDTO.getEmail());
        log.debug("Dados recebidos para registro: {}", registroDTO);
        
        if (usuarioRepository.existsByEmail(registroDTO.getEmail())) {
            log.warn("Tentativa de registro falhou: Email {} já cadastrado.", registroDTO.getEmail());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email já cadastrado");
        }
        
        if (usuarioRepository.existsByCpf(registroDTO.getCpf())) {
            log.warn("Tentativa de registro falhou: CPF {} já cadastrado.", registroDTO.getCpf());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CPF já cadastrado");
        }
        
        Usuario usuario;
        if (registroDTO.getTipoUsuario() == TipoUsuario.PACIENTE) {
            log.info("Criando novo usuário PACIENTE");
            usuario = criarPaciente(registroDTO);
        } else {
            log.info("Criando novo usuário MEDICO");
            if (registroDTO.getCrm() == null || registroDTO.getCrm().isBlank() || 
                registroDTO.getCrmEstado() == null || registroDTO.getCrmEstado().isBlank()) {
                log.warn("Tentativa de registro de MÉDICO falhou: CRM ou Estado do CRM não fornecidos.");
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CRM e Estado do CRM são obrigatórios para médicos.");
            }
            usuario = criarMedico(registroDTO);
        }
        
        log.info("Codificando senha para usuário {}", usuario.getEmail());
        usuario.setSenha(passwordEncoder.encode(registroDTO.getSenha()));
        
        log.info("Salvando usuário {} no banco de dados", usuario.getEmail());
        try {
            usuarioRepository.save(usuario);
            log.info("Usuário {} salvo com sucesso.", usuario.getEmail());
        } catch (Exception e) {
            log.error("Erro ao salvar usuário {} no banco de dados: {}", usuario.getEmail(), e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno ao salvar usuário.", e);
        }
        
        log.info("Gerando token JWT para usuário {}", usuario.getEmail());
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