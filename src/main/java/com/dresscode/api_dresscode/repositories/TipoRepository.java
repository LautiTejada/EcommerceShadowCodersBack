package com.dresscode.api_dresscode.repositories;

import com.dresscode.api_dresscode.entities.Tipo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TipoRepository extends BaseRepository<Tipo, Long> {
    Optional<Tipo> findByNombre(String nombre);
}
