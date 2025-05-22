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

public class TalleService extends BaseServiceImpl<Talle, Long> {
    private final TalleRepository talleRepository;
    private final ProductoRepository productoRepository;
    private final ProductoTalleRepository productoTalleRepository;

    @Override
    protected JpaRepository<Talle, Long> getRepository() {
        return talleRepository;
    }





    public void asignarTalleAProducto(Long productoId, Long talleId) {
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + productoId));

        Talle talle = talleRepository.findById(talleId)
                .orElseThrow(() -> new RuntimeException("Talle no encontrado con id: " + talleId));

        // Validamos que no exista ya esta relación
        if (productoTalleRepository.existsByProductoIdAndTalleId(productoId, talleId)) {
            throw new RuntimeException("El producto ya tiene asignado este talle.");
        }

        ProductoTalle productoTalle = ProductoTalle.builder()
                .producto(producto)
                .talle(talle)
                .build();

        productoTalleRepository.save(productoTalle);
    }

    public void eliminarTalleDeProducto(Long productoId, Long talleId) {
        ProductoTalle productoTalle = productoTalleRepository.findByProductoIdAndTalleId(productoId, talleId)
                .orElseThrow(() -> new RuntimeException("No se encuentra esta relación producto-talle."));

        productoTalleRepository.delete(productoTalle);
    }
}
