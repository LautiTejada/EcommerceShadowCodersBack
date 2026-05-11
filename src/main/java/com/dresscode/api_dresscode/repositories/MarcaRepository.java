package com.dresscode.api_dresscode.repositories;

import com.dresscode.api_dresscode.entities.Marca;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface MarcaRepository extends BaseRepository<Marca, Long> {
    Optional<Marca> findByNombreMarca(String nombreMarca);
}
