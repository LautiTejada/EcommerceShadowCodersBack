package com.dresscode.api_dresscode.repositories;

import com.dresscode.api_dresscode.entities.DescuentoProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DescuentoProductoRepository extends BaseRepository<DescuentoProducto, Long> {
}
