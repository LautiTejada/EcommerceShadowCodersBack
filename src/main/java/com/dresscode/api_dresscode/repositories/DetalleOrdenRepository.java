package com.dresscode.api_dresscode.repositories;

import com.dresscode.api_dresscode.entities.DetalleOrden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetalleOrdenRepository extends JpaRepository<DetalleOrden, Long> {
    List<DetalleOrden> findByOrdenDeCompraId(Long ordenId);
}
