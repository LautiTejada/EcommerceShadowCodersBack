package com.dresscode.api_dresscode.repositories;

import com.dresscode.api_dresscode.entities.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
}
