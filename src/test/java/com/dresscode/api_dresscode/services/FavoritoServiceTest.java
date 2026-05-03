package com.dresscode.api_dresscode.services;

import com.dresscode.api_dresscode.dtos.FavoritoDTO;
import com.dresscode.api_dresscode.entities.Favorito;
import com.dresscode.api_dresscode.entities.Producto;
import com.dresscode.api_dresscode.entities.Usuario;
import com.dresscode.api_dresscode.repositories.FavoritoRepository;
import com.dresscode.api_dresscode.repositories.ProductoRepository;
import com.dresscode.api_dresscode.repositories.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FavoritoServiceTest {

    @Mock
    private FavoritoRepository favoritoRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private FavoritoService favoritoService;

    private Usuario usuario;
    private Producto producto;

    public FavoritoServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("testuser");

        producto = new Producto();
        producto.setId(2L);
        producto.setNombre("Remera");
        producto.setPrecio(500.0);
    }

    @Test
    void testAgregarAFavoritos_NuevoFavorito() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(productoRepository.findById(2L)).thenReturn(Optional.of(producto));
        when(favoritoRepository.existsByUsuarioAndProductoAndActivoTrue(usuario, producto)).thenReturn(false);
        when(favoritoRepository.findByUsuarioAndProducto(usuario, producto)).thenReturn(Optional.empty());

        Favorito favoritoGuardado = Favorito.builder()
                .usuario(usuario)
                .producto(producto)
                .fechaAgregado(LocalDateTime.now())
                .build();
        favoritoGuardado.setId(10L);
        favoritoGuardado.setActivo(true);

        when(favoritoRepository.save(any(Favorito.class))).thenReturn(favoritoGuardado);

        FavoritoDTO resultado = favoritoService.agregarAFavoritos(1L, 2L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getUsuarioId());
        assertEquals(2L, resultado.getProductoId());
        assertEquals("Remera", resultado.getNombreProducto());
        verify(favoritoRepository, times(1)).save(any(Favorito.class));
    }

    @Test
    void testAgregarAFavoritos_YaExiste_LanzaExcepcion() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(productoRepository.findById(2L)).thenReturn(Optional.of(producto));
        when(favoritoRepository.existsByUsuarioAndProductoAndActivoTrue(usuario, producto)).thenReturn(true);

        assertThrows(RuntimeException.class, () -> favoritoService.agregarAFavoritos(1L, 2L));
        verify(favoritoRepository, never()).save(any());
    }

    @Test
    void testAgregarAFavoritos_Reactivacion() {
        Favorito favoritoInactivo = Favorito.builder()
                .usuario(usuario)
                .producto(producto)
                .fechaAgregado(LocalDateTime.now().minusDays(5))
                .build();
        favoritoInactivo.setId(10L);
        favoritoInactivo.setActivo(false);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(productoRepository.findById(2L)).thenReturn(Optional.of(producto));
        when(favoritoRepository.existsByUsuarioAndProductoAndActivoTrue(usuario, producto)).thenReturn(false);
        when(favoritoRepository.findByUsuarioAndProducto(usuario, producto)).thenReturn(Optional.of(favoritoInactivo));
        when(favoritoRepository.save(any(Favorito.class))).thenAnswer(inv -> inv.getArgument(0));

        FavoritoDTO resultado = favoritoService.agregarAFavoritos(1L, 2L);

        assertNotNull(resultado);
        assertTrue(favoritoInactivo.getActivo());
        verify(favoritoRepository, times(1)).save(favoritoInactivo);
    }

    @Test
    void testEliminarDeFavoritos_HappyPath() {
        Favorito favorito = Favorito.builder()
                .usuario(usuario)
                .producto(producto)
                .fechaAgregado(LocalDateTime.now())
                .build();
        favorito.setId(10L);
        favorito.setActivo(true);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(productoRepository.findById(2L)).thenReturn(Optional.of(producto));
        when(favoritoRepository.findByUsuarioAndProducto(usuario, producto)).thenReturn(Optional.of(favorito));
        when(favoritoRepository.save(any(Favorito.class))).thenReturn(favorito);

        favoritoService.eliminarDeFavoritos(1L, 2L);

        assertFalse(favorito.getActivo());
        verify(favoritoRepository, times(1)).save(favorito);
    }

    @Test
    void testEliminarDeFavoritos_NoExiste_LanzaExcepcion() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(productoRepository.findById(2L)).thenReturn(Optional.of(producto));
        when(favoritoRepository.findByUsuarioAndProducto(usuario, producto)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> favoritoService.eliminarDeFavoritos(1L, 2L));
    }

    @Test
    void testObtenerFavoritosDelUsuario() {
        Favorito favorito = Favorito.builder()
                .usuario(usuario)
                .producto(producto)
                .fechaAgregado(LocalDateTime.now())
                .build();
        favorito.setId(10L);
        favorito.setActivo(true);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(favoritoRepository.findByUsuarioAndActivoTrue(usuario)).thenReturn(List.of(favorito));

        List<FavoritoDTO> resultado = favoritoService.obtenerFavoritosDelUsuario(1L);

        assertEquals(1, resultado.size());
        assertEquals("Remera", resultado.get(0).getNombreProducto());
        assertEquals(500.0, resultado.get(0).getPrecioProducto());
    }

    @Test
    void testEsFavorito_Verdadero() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(productoRepository.findById(2L)).thenReturn(Optional.of(producto));
        when(favoritoRepository.existsByUsuarioAndProductoAndActivoTrue(usuario, producto)).thenReturn(true);

        assertTrue(favoritoService.esFavorito(1L, 2L));
    }

    @Test
    void testEsFavorito_Falso() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(productoRepository.findById(2L)).thenReturn(Optional.of(producto));
        when(favoritoRepository.existsByUsuarioAndProductoAndActivoTrue(usuario, producto)).thenReturn(false);

        assertFalse(favoritoService.esFavorito(1L, 2L));
    }

    @Test
    void testEsElMismoUsuario_Verdadero() {
        when(usuarioRepository.findByUsername("testuser")).thenReturn(Optional.of(usuario));

        assertTrue(favoritoService.esElMismoUsuario(1L, "testuser"));
    }

    @Test
    void testEsElMismoUsuario_Falso() {
        Usuario otroUsuario = new Usuario();
        otroUsuario.setId(99L);
        when(usuarioRepository.findByUsername("otrouser")).thenReturn(Optional.of(otroUsuario));

        assertFalse(favoritoService.esElMismoUsuario(1L, "otrouser"));
    }
}
