package com.dresscode.api_dresscode.repositories;

import com.dresscode.api_dresscode.entities.ProductoTalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoTalleRepository extends JpaRepository<ProductoTalle,Long> {
    boolean existsByProductoIdAndTalleId(Long productoId, Long talleId);
    Optional<ProductoTalle> findByProductoIdAndTalleId(Long productoId, Long talleId);
    List<ProductoTalle> findByProductoId(Long productoId);
}
