package com.dresscode.api_dresscode.repositories;

import com.dresscode.api_dresscode.entities.Descuento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DescuentoRepository extends JpaRepository<Descuento, Long> {
}
