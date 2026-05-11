package com.dresscode.api_dresscode.repositories;

import com.dresscode.api_dresscode.entities.Color;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ColorRepository extends BaseRepository<Color, Long> {
    Optional<Color> findByNombreColor(String nombreColor);
}
