package com.dresscode.api_dresscode.repositories;

import com.dresscode.api_dresscode.entities.Direccion;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DireccionRepository extends BaseRepository<Direccion, Long> {

    /**
     * Returns all addresses belonging to a given user ID.
     * Preferred over accessing {@code usuario.getDirecciones()} on a LAZY association
     * outside of a transaction context.
     */
    List<Direccion> findByUsuarioId(Long usuarioId);

    /**
     * Returns the first active address for a user, filtered at the DB level.
     * Replaces the in-memory {@code usuario.getDirecciones().stream().filter(Direccion::getActivo).findFirst()}
     * pattern in OrdenDeCompraService — avoids loading all addresses into the JVM heap.
     */
    Optional<Direccion> findFirstByUsuarioIdAndActivoTrue(Long usuarioId);
}
