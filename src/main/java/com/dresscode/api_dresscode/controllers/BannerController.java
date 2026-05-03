package com.dresscode.api_dresscode.controllers;

import com.dresscode.api_dresscode.dtos.BannerDTO;
import com.dresscode.api_dresscode.entities.Banner;
import com.dresscode.api_dresscode.services.BannerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/banners")
@RequiredArgsConstructor
public class BannerController {

    private final BannerService bannerService;

    /**
     * Obtiene todos los banners activos (público, sin autenticación)
     */
    @GetMapping
    public ResponseEntity<?> getAll() throws Exception {
        List<Banner> banners = bannerService.obtenerBanneresActivos();
        return ResponseEntity.ok(banners);
    }

    /**
     * Obtiene todos los banners activos con paginación (público)
     */
    @GetMapping("/paged")
    public ResponseEntity<?> getAll(Pageable pageable) throws Exception {
        Page<Banner> banners = bannerService.obtenerBanneresActivosPaginado(pageable);
        return ResponseEntity.ok(banners);
    }

    /**
     * Obtiene un banner activo por ID (público)
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getOne(@PathVariable Long id) throws Exception {
        Banner banner = bannerService.obtenerBannerActivo(id);
        return ResponseEntity.ok(banner);
    }

    /**
     * Crea un nuevo banner (solo ADMIN)
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> save(@Valid @RequestBody BannerDTO dto) throws Exception {
        Banner saved = bannerService.crearBanner(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    /**
     * Actualiza un banner existente (solo ADMIN)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody BannerDTO dto) throws Exception {
        Banner updated = bannerService.actualizarBanner(id, dto);
        return ResponseEntity.ok(updated);
    }

    /**
     * Elimina un banner (soft delete, solo ADMIN)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> delete(@PathVariable Long id) throws Exception {
        boolean deleted = bannerService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Cambia el estado activo/inactivo de un banner (solo ADMIN)
     */
    @PatchMapping("/{id}/toggle")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> changeStatus(@PathVariable Long id) throws Exception {
        Banner banner = bannerService.changeStatus(id);
        return ResponseEntity.ok(banner);
    }
}
