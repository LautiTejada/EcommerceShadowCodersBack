package com.dresscode.api_dresscode.services;

import com.dresscode.api_dresscode.entities.Producto;
import com.dresscode.api_dresscode.entities.ProductoTalle;
import com.dresscode.api_dresscode.entities.Talle;
import com.dresscode.api_dresscode.repositories.ProductoRepository;
import com.dresscode.api_dresscode.repositories.ProductoTalleRepository;
import com.dresscode.api_dresscode.repositories.TalleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor

public class ProductoTalleService extends BaseServiceImpl<ProductoTalle, Long> {

    private final ProductoTalleRepository productoTalleRepository;
    private final ProductoRepository productoRepository;
    private final TalleRepository talleRepository;

    @Override
    protected JpaRepository<ProductoTalle,Long> getRepository() {
        return productoTalleRepository;
    }


    @Transactional
    public ProductoTalle crearProductoTalle(Long productoId, Long talleId, Integer cantidad) {
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        Talle talle = talleRepository.findById(talleId)
                .orElseThrow(() -> new RuntimeException("Talle no encontrado"));

        ProductoTalle productoTalle = ProductoTalle.builder()
                .producto(producto)
                .talle(talle)
                .cantidad(cantidad)
                .build();

        return productoTalleRepository.save(productoTalle);
    }

    public ProductoTalle actualizarCantidad(Long productoTalleId, Integer nuevaCantidad) {
        ProductoTalle productoTalle = productoTalleRepository.findById(productoTalleId)
                .orElseThrow(() -> new RuntimeException("ProductoTalle no encontrado"));

        productoTalle.setCantidad(nuevaCantidad);
        return productoTalleRepository.save(productoTalle);
    }

    public List<ProductoTalle> obtenerTallesPorProducto(Long productoId) {
        return productoTalleRepository.findByProductoId(productoId);
    }


    public Integer obtenerCantidadTotalDeProducto(Long productoId) {
        List<ProductoTalle> talles = productoTalleRepository.findByProductoId(productoId);
        return talles.stream()
                .mapToInt(ProductoTalle::getCantidad)
                .sum();
    }




}
