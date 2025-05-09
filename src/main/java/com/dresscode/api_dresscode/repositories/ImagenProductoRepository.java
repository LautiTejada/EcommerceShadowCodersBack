package com.dresscode.api_dresscode.repositories;

import com.dresscode.api_dresscode.entities.ImagenProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImagenProductoRepository extends JpaRepository<ImagenProducto, Long> {
    List<ImagenProducto> findByProductoId(Long productoId);
    boolean existsByProductoIdAndPrincipalTrue(Long productoId);
}
