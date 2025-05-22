package com.dresscode.api_dresscode.repositories;

import com.dresscode.api_dresscode.entities.OrdenDeCompra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdenDeCompraRepository extends BaseRepository<OrdenDeCompra, Long> {
    List<OrdenDeCompra> findByUsuarioId(Long usuarioId);
}
