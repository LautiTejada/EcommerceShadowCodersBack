package com.dresscode.api_dresscode.repositories;

import com.dresscode.api_dresscode.entities.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    List<Categoria> findByTipoId(Long tipoId);
}
