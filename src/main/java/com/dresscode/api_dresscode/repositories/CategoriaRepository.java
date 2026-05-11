package com.dresscode.api_dresscode.repositories;

import com.dresscode.api_dresscode.entities.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriaRepository extends BaseRepository<Categoria, Long> {

    List<Categoria> findByTipoId(Long tipoId);
    List<Categoria> findByTipoIdIn(List<Long> tipoIds);
    Optional<Categoria> findByNombreCategoria(String nombreCategoria);
}
