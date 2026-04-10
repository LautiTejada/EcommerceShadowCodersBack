package com.dresscode.api_dresscode.repositories;

import com.dresscode.api_dresscode.entities.Favorito;
import com.dresscode.api_dresscode.entities.Usuario;
import com.dresscode.api_dresscode.entities.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoritoRepository extends JpaRepository<Favorito, Long> {
    List<Favorito> findByUsuarioAndActivoTrue(Usuario usuario);
    Optional<Favorito> findByUsuarioAndProducto(Usuario usuario, Producto producto);
    boolean existsByUsuarioAndProductoAndActivoTrue(Usuario usuario, Producto producto);
    void deleteByUsuarioAndProducto(Usuario usuario, Producto producto);
}
