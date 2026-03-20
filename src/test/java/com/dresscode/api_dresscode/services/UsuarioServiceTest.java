package com.dresscode.api_dresscode.services;

import com.dresscode.api_dresscode.entities.Usuario;
import com.dresscode.api_dresscode.repositories.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    public UsuarioServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveUsuario() {
        Usuario usuario = Usuario.builder()
                .username("testuser")
                .email("test@email.com")
                .password("password")
                .rol(Usuario.Rol.USER)
                .build();
        when(usuarioRepository.findByEmail("test@email.com")).thenReturn(java.util.Optional.empty());
        when(usuarioRepository.save(any())).thenReturn(usuario);

        Usuario result = usuarioService.save(usuario);
        assertEquals("testuser", result.getUsername());
        assertEquals("test@email.com", result.getEmail());
        assertEquals(Usuario.Rol.USER, result.getRol());
    }
}
