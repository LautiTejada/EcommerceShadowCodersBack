package com.dresscode.api_dresscode.controllers;

import com.dresscode.api_dresscode.dtos.FavoritoDTO;
import com.dresscode.api_dresscode.services.FavoritoService;
import com.dresscode.api_dresscode.services.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FavoritoControllerTest {

    @Mock
    private FavoritoService favoritoService;
    @Mock
    private UsuarioService usuarioService;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;
    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private FavoritoController favoritoController;

    public FavoritoControllerTest() {
        MockitoAnnotations.openMocks(this);
    }

    @BeforeEach
    void setUpSecurityContext() {
        when(userDetails.getUsername()).thenReturn("testuser");
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(usuarioService.obtenerIdPorUsername("testuser")).thenReturn(1L);
    }

    @Test
    void testMisFavoritos() {
        FavoritoDTO dto = FavoritoDTO.builder()
                .id(1L)
                .usuarioId(1L)
                .productoId(2L)
                .nombreProducto("Remera")
                .precioProducto(500.0)
                .fechaAgregado(LocalDateTime.now())
                .build();

        when(favoritoService.obtenerFavoritosDelUsuario(1L)).thenReturn(List.of(dto));

        ResponseEntity<List<FavoritoDTO>> response = favoritoController.misFavoritos();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Remera", response.getBody().get(0).getNombreProducto());
    }

    @Test
    void testAgregarAFavoritos() {
        FavoritoDTO dto = FavoritoDTO.builder()
                .id(10L)
                .usuarioId(1L)
                .productoId(2L)
                .nombreProducto("Remera")
                .precioProducto(500.0)
                .fechaAgregado(LocalDateTime.now())
                .build();

        when(favoritoService.agregarAFavoritos(1L, 2L)).thenReturn(dto);

        ResponseEntity<FavoritoDTO> response = favoritoController.agregarAFavoritos(2L);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(10L, response.getBody().getId());
    }

    @Test
    void testEliminarDeFavoritos() {
        doNothing().when(favoritoService).eliminarDeFavoritos(1L, 2L);

        ResponseEntity<?> response = favoritoController.eliminarDeFavoritos(2L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(favoritoService, times(1)).eliminarDeFavoritos(1L, 2L);
    }

    @Test
    void testEsFavorito_Verdadero() {
        when(favoritoService.esFavorito(1L, 2L)).thenReturn(true);

        ResponseEntity<Boolean> response = favoritoController.esFavorito(2L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody());
    }

    @Test
    void testEsFavorito_Falso() {
        when(favoritoService.esFavorito(1L, 2L)).thenReturn(false);

        ResponseEntity<Boolean> response = favoritoController.esFavorito(2L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody());
    }
}
