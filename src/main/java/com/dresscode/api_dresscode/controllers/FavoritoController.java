package com.dresscode.api_dresscode.controllers;

import com.dresscode.api_dresscode.entities.Favorito;
import com.dresscode.api_dresscode.services.FavoritoService;
import com.dresscode.api_dresscode.services.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favoritos")
@RequiredArgsConstructor
public class FavoritoController {

    private final FavoritoService favoritoService;
    private final UsuarioService usuarioService;

    /**
     * Obtiene todos los favoritos del usuario autenticado
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Favorito>> misFavoritos() {
        Long usuarioId = obtenerIdUsuarioAutenticado();
        List<Favorito> favoritos = favoritoService.obtenerFavoritosDelUsuario(usuarioId);
        return ResponseEntity.ok(favoritos);
    }

    /**
     * Obtiene todos los favoritos activos de un usuario específico
     */
    @GetMapping("/usuario/{usuarioId}")
    @PreAuthorize("hasRole('ADMIN') or @favoritoService.esElMismoUsuario(#usuarioId, authentication.principal.username)")
    public ResponseEntity<List<Favorito>> favoritosDeUsuario(@PathVariable Long usuarioId) {
        List<Favorito> favoritos = favoritoService.obtenerFavoritosDelUsuario(usuarioId);
        return ResponseEntity.ok(favoritos);
    }

    /**
     * Verifica si un producto está en los favoritos del usuario autenticado
     */
    @GetMapping("/producto/{productoId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Boolean> esFavorito(@PathVariable Long productoId) {
        Long usuarioId = obtenerIdUsuarioAutenticado();
        boolean esFavorito = favoritoService.esFavorito(usuarioId, productoId);
        return ResponseEntity.ok(esFavorito);
    }

    /**
     * Agrega un producto a los favoritos del usuario autenticado
     */
    @PostMapping("/{productoId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Favorito> agregarAFavoritos(@PathVariable Long productoId) {
        Long usuarioId = obtenerIdUsuarioAutenticado();
        Favorito favorito = favoritoService.agregarAFavoritos(usuarioId, productoId);
        return ResponseEntity.status(HttpStatus.CREATED).body(favorito);
    }

    /**
     * Elimina un producto de los favoritos del usuario autenticado
     */
    @DeleteMapping("/{productoId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> eliminarDeFavoritos(@PathVariable Long productoId) {
        Long usuarioId = obtenerIdUsuarioAutenticado();
        favoritoService.eliminarDeFavoritos(usuarioId, productoId);
        return ResponseEntity.ok().build();
    }

    /**
     * Obtiene el ID del usuario autenticado desde el token JWT
     */
    private Long obtenerIdUsuarioAutenticado() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            return usuarioService.obtenerIdPorUsername(username);
        }
        throw new RuntimeException("No se pudo obtener el ID del usuario autenticado");
    }
}
