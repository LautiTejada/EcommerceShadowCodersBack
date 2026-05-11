package com.dresscode.api_dresscode.services;

import com.dresscode.api_dresscode.entities.Direccion;
import com.dresscode.api_dresscode.entities.Usuario;
import com.dresscode.api_dresscode.repositories.DireccionRepository;
import com.dresscode.api_dresscode.repositories.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class DireccionServiceTest {

    @Mock
    private DireccionRepository direccionRepository;
    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private DireccionService direccionService;

    public DireccionServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    // No existe el método crearDireccionYAsignarAUsuario en DireccionService
    // Se puede testear el save de Direccion
    @Test
    void testSaveDireccion() {
        Direccion direccion = new Direccion();
        when(direccionRepository.save(any())).thenReturn(direccion);
        Direccion result = direccionService.save(direccion);
        assertEquals(direccion, result);
    }
}
