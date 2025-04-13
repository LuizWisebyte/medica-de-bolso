package br.com.medicadebolso.domain.service;

import br.com.medicadebolso.domain.model.Usuario;
import br.com.medicadebolso.domain.dto.LoginDTO;
import br.com.medicadebolso.domain.dto.RegistroDTO;
import br.com.medicadebolso.domain.repository.UsuarioRepository;
import br.com.medicadebolso.domain.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;
    
    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthService authService;

    private RegistroDTO registroRequest;
    private LoginDTO loginRequest;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        registroRequest = new RegistroDTO();
        registroRequest.setNome("Test User");
        registroRequest.setEmail("test@example.com");
        registroRequest.setSenha("password123");
        registroRequest.setCpf("12345678900"); 
        registroRequest.setTipoUsuario(Usuario.TipoUsuario.PACIENTE);

        loginRequest = new LoginDTO();
        loginRequest.setEmail("test@example.com");
        loginRequest.setSenha("password123");

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Test User");
        usuario.setEmail("test@example.com");
        usuario.setSenha("encodedPassword");
    }

    @Test
    void registrar_ShouldReturnToken_WhenValidRequest() {
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);
        when(usuarioRepository.existsByCpf(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);
        when(jwtService.gerarToken(any(Usuario.class))).thenReturn("jwtToken");

        String token = authService.registrar(registroRequest);

        assertNotNull(token);
        assertEquals("jwtToken", token);
        verify(usuarioRepository).save(any(Usuario.class));
        verify(jwtService).gerarToken(any(Usuario.class));
    }

    @Test
    void login_ShouldReturnToken_WhenValidCredentials() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(usuario);
        when(jwtService.gerarToken(any(Usuario.class))).thenReturn("jwtToken");

        String token = authService.login(loginRequest);

        assertNotNull(token);
        assertEquals("jwtToken", token);
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService).gerarToken(any(Usuario.class));
    }
} 