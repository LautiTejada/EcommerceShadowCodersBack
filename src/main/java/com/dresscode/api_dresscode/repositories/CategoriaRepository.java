package com.dresscode.api_dresscode.repositories;

import com.dresscode.api_dresscode.entities.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
}
