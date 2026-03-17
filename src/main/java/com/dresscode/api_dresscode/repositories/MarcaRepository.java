package com.dresscode.api_dresscode.repositories;

import com.dresscode.api_dresscode.entities.Marca;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface MarcaRepository extends JpaRepository<Marca, Long> {
    Optional<Marca> findByNombreMarca(String nombreMarca);
}
