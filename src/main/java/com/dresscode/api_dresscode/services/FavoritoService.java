package com.dresscode.api_dresscode.services;

import com.dresscode.api_dresscode.dtos.FavoritoDTO;
import com.dresscode.api_dresscode.entities.Favorito;
import com.dresscode.api_dresscode.entities.Producto;
import com.dresscode.api_dresscode.entities.Usuario;
import com.dresscode.api_dresscode.repositories.BaseRepository;
import com.dresscode.api_dresscode.repositories.FavoritoRepository;
import com.dresscode.api_dresscode.repositories.ProductoRepository;
import com.dresscode.api_dresscode.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoritoService extends BaseServiceImpl<Favorito, Long> {

    private final FavoritoRepository favoritoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;

    @Override
    protected BaseRepository<Favorito, Long> getRepository() {
        return favoritoRepository;
    }

    private FavoritoDTO toDTO(Favorito favorito) {
        return FavoritoDTO.builder()
                .id(favorito.getId())
                .usuarioId(favorito.getUsuario().getId())
                .productoId(favorito.getProducto().getId())
                .nombreProducto(favorito.getProducto().getNombre())
                .precioProducto(favorito.getProducto().getPrecio())
                .fechaAgregado(favorito.getFechaAgregado())
                .build();
    }

    /**
     * Agrega un producto a los favoritos del usuario autenticado
     * Si el favorito fue eliminado anteriormente, lo reactiva
     */
    @Transactional
    public FavoritoDTO agregarAFavoritos(Long usuarioId, Long productoId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        // Verificar si ya existe el favorito activo
        if (favoritoRepository.existsByUsuarioAndProductoAndActivoTrue(usuario, producto)) {
            throw new RuntimeException("El producto ya está en favoritos");
        }

        // Buscar si existe pero está inactivo (fue eliminado previamente)
        var favoritoExistente = favoritoRepository.findByUsuarioAndProducto(usuario, producto);
        
        if (favoritoExistente.isPresent()) {
            // Reactivar el favorito existente
            Favorito favorito = favoritoExistente.get();
            favorito.setActivo(true);
            favorito.setFechaAgregado(LocalDateTime.now());
            return toDTO(favoritoRepository.save(favorito));
        }

        // Crear nuevo favorito
        Favorito favorito = Favorito.builder()
                .usuario(usuario)
                .producto(producto)
                .fechaAgregado(LocalDateTime.now())
                .build();
        
        favorito.setActivo(true);
        return toDTO(favoritoRepository.save(favorito));
    }

    /**
     * Elimina un producto de los favoritos del usuario
     */
    @Transactional
    public void eliminarDeFavoritos(Long usuarioId, Long productoId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        Favorito favorito = favoritoRepository.findByUsuarioAndProducto(usuario, producto)
                .orElseThrow(() -> new RuntimeException("Favorito no encontrado"));

        favorito.setActivo(false);
        favoritoRepository.save(favorito);
    }

    /**
     * Obtiene todos los favoritos activos del usuario
     */
    public List<FavoritoDTO> obtenerFavoritosDelUsuario(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Uses JOIN FETCH to avoid N+1: all Productos are loaded in a single query.
        return favoritoRepository.findActivosByUsuarioWithProducto(usuario)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Verifica si un producto está en los favoritos del usuario
     */
    public boolean esFavorito(Long usuarioId, Long productoId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        return favoritoRepository.existsByUsuarioAndProductoAndActivoTrue(usuario, producto);
    }

    /**
     * Verifica si el usuario autenticado es el propietario de los favoritos
     */
    public boolean esElMismoUsuario(Long usuarioId, String username) {
        Usuario usuarioAutenticado = usuarioRepository.findByUsername(username).orElse(null);
        if (usuarioAutenticado == null) {
            return false;
        }
        return usuarioAutenticado.getId().equals(usuarioId);
    }
}
