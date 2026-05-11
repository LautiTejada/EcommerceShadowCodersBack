package com.dresscode.api_dresscode.repositories;

import com.dresscode.api_dresscode.entities.Banner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BannerRepository extends BaseRepository<Banner, Long> {
    
    /**
     * Obtiene todos los banners activos ordenados por orden ascendente
     */
    List<Banner> findByActivoTrueOrderByOrdenAsc();
    
    /**
     * Obtiene todos los banners activos con paginación
     */
    Page<Banner> findByActivoTrue(Pageable pageable);
    
    /**
     * Obtiene un banner activo por ID
     */
    Optional<Banner> findByIdAndActivoTrue(Long id);
    
    /**
     * Verifica si existe un banner activo con un orden específico
     */
    boolean existsByOrdenAndActivoTrueAndIdNot(Integer orden, Long id);
}
