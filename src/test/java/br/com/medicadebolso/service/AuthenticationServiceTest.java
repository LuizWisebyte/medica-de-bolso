package br.com.medicadebolso.service;

import br.com.medicadebolso.domain.Usuario;
import br.com.medicadebolso.domain.dto.AuthenticationRequest;
import br.com.medicadebolso.domain.dto.AuthenticationResponse;
import br.com.medicadebolso.domain.dto.RegisterRequest;
import br.com.medicadebolso.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationService authenticationService;

    private RegisterRequest registerRequest;
    private AuthenticationRequest authRequest;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        registerRequest = RegisterRequest.builder()
                .nome("Test User")
                .email("test@example.com")
                .password("password123")
                .build();

        authRequest = AuthenticationRequest.builder()
                .email("test@example.com")
                .password("password123")
                .build();

        usuario = Usuario.builder()
                .id(UUID.randomUUID())
                .nome("Test User")
                .email("test@example.com")
                .password("encodedPassword")
                .build();
    }

    @Test
    void register_ShouldReturnToken_WhenValidRequest() {
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        when(usuarioRepository.save(any())).thenReturn(usuario);
        when(jwtService.generateToken(any())).thenReturn("jwtToken");

        AuthenticationResponse response = authenticationService.register(registerRequest);

        assertNotNull(response);
        assertEquals("jwtToken", response.getToken());
        verify(usuarioRepository).save(any());
        verify(jwtService).generateToken(any());
    }

    @Test
    void authenticate_ShouldReturnToken_WhenValidCredentials() {
        when(usuarioRepository.findByEmail(any())).thenReturn(Optional.of(usuario));
        when(jwtService.generateToken(any())).thenReturn("jwtToken");

        AuthenticationResponse response = authenticationService.authenticate(authRequest);

        assertNotNull(response);
        assertEquals("jwtToken", response.getToken());
        verify(authenticationManager).authenticate(any());
        verify(jwtService).generateToken(any());
    }
} 