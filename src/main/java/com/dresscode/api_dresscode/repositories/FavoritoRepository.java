package com.dresscode.api_dresscode.repositories;

import com.dresscode.api_dresscode.entities.Favorito;
import com.dresscode.api_dresscode.entities.Usuario;
import com.dresscode.api_dresscode.entities.Producto;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoritoRepository extends BaseRepository<Favorito, Long> {

    List<Favorito> findByUsuarioAndActivoTrue(Usuario usuario);

    Optional<Favorito> findByUsuarioAndProducto(Usuario usuario, Producto producto);

    boolean existsByUsuarioAndProductoAndActivoTrue(Usuario usuario, Producto producto);

    void deleteByUsuarioAndProducto(Usuario usuario, Producto producto);

    /**
     * Loads active Favoritos for a user with their associated Productos fetched in a single query.
     * Uses JOIN FETCH to eliminate N+1: without this, accessing favorito.getProducto() in a loop
     * would fire one SELECT per Producto.
     */
    @Query("SELECT f FROM Favorito f JOIN FETCH f.producto WHERE f.usuario = :usuario AND f.activo = true")
    List<Favorito> findActivosByUsuarioWithProducto(@Param("usuario") Usuario usuario);
}
