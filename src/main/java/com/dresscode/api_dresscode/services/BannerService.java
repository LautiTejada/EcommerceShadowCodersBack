package com.dresscode.api_dresscode.services;

import com.dresscode.api_dresscode.dtos.BannerDTO;
import com.dresscode.api_dresscode.entities.Banner;
import com.dresscode.api_dresscode.entities.Marca;
import com.dresscode.api_dresscode.repositories.BannerRepository;
import com.dresscode.api_dresscode.repositories.MarcaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BannerService extends BaseServiceImpl<Banner, Long> {

    private final BannerRepository bannerRepository;
    private final MarcaRepository marcaRepository;

    @Override
    protected JpaRepository<Banner, Long> getRepository() {
        return bannerRepository;
    }

    /**
     * Obtiene todos los banners activos ordenados por orden
     */
    public List<Banner> obtenerBanneresActivos() {
        return bannerRepository.findByActivoTrueOrderByOrdenAsc();
    }

    /**
     * Obtiene todos los banners activos con paginación
     */
    public Page<Banner> obtenerBanneresActivosPaginado(Pageable pageable) {
        return bannerRepository.findByActivoTrue(pageable);
    }

    /**
     * Obtiene un banner activo por ID
     */
    public Banner obtenerBannerActivo(Long id) {
        return bannerRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new RuntimeException("Banner no encontrado"));
    }

    /**
     * Crea nuevo banner desde DTO
     */
    @Transactional
    public Banner crearBanner(BannerDTO dto) {
        Banner banner = convertirDTOaEntity(dto);
        return bannerRepository.save(banner);
    }

    /**
     * Actualiza banner existente desde DTO
     */
    @Transactional
    public Banner actualizarBanner(Long id, BannerDTO dto) {
        Banner banner = obtenerBannerActivo(id);
        return actualizarDesdeDTO(banner, dto);
    }

    /**
     * Elimina un banner (soft delete)
     */
    @Override
    @Transactional
    public boolean delete(Long id) {
        Banner banner = obtenerBannerActivo(id);
        banner.setActivo(false);
        bannerRepository.save(banner);
        return true;
    }

    /**
     * Cambia el estado activo/inactivo de un banner
     */
    @Override
    @Transactional
    public Banner changeStatus(Long id) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Banner no encontrado"));
        banner.setActivo(!banner.getActivo());
        return bannerRepository.save(banner);
    }

    /**
     * Convierte BannerDTO a Banner (privado - reutilizable)
     */
    private Banner convertirDTOaEntity(BannerDTO dto) {
        Marca marca = obtenerMarcaValida(dto.getMarcaId());
        Banner banner = new Banner();
        banner.setTitulo(dto.getTitulo());
        banner.setImagenNombre(dto.getImagenNombre());
        banner.setMarca(marca);
        banner.setOrden(dto.getOrden());
        banner.setActivo(true);
        return banner;
    }

    /**
     * Actualiza banner con datos de DTO
     */
    private Banner actualizarDesdeDTO(Banner banner, BannerDTO dto) {
        if (dto.getTitulo() != null && !dto.getTitulo().isBlank()) {
            banner.setTitulo(dto.getTitulo());
        }
        if (dto.getMarcaId() != null) {
            banner.setMarca(obtenerMarcaValida(dto.getMarcaId()));
        }
        if (dto.getOrden() != null && dto.getOrden() > 0) {
            banner.setOrden(dto.getOrden());
        }
        if (dto.getActivo() != null) {
            banner.setActivo(dto.getActivo());
        }
        return bannerRepository.save(banner);
    }

    /**
     * Obtiene y valida Marca por ID - Centraliza lógica repetida
     */
    private Marca obtenerMarcaValida(Long marcaId) {
        return marcaRepository.findById(marcaId)
                .orElseThrow(() -> new RuntimeException("La marca con id " + marcaId + " no existe"));
    }
}
