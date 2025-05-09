package com.dresscode.api_dresscode.services;

import com.dresscode.api_dresscode.entities.Producto;
import com.dresscode.api_dresscode.entities.ProductoTalle;
import com.dresscode.api_dresscode.entities.Talle;
import com.dresscode.api_dresscode.repositories.ProductoRepository;
import com.dresscode.api_dresscode.repositories.ProductoTalleRepository;
import com.dresscode.api_dresscode.repositories.TalleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor

public class TalleService {
    private TalleRepository talleRepository;
    private final ProductoRepository productoRepository;
    private final ProductoTalleRepository productoTalleRepository;

    public List<Talle> getAllTalles() {
        return talleRepository.findAll();
    }

    public Talle getTalleById(Long id) {
        return talleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Talle no encontrado con id: " + id));
    }

    public Talle createTalle(Talle talle) {
        return talleRepository.save(talle);
    }

    @Transactional
    public Talle updateTalle(Long id, Talle talleActualizado) {
        Talle talleExistente = getTalleById(id);
        talleExistente.setTipoTalle(talleActualizado.getTipoTalle());
        return talleRepository.save(talleExistente);
    }

    @Transactional
    public void deleteTalle(Long id) {
        Talle talle = getTalleById(id);
        talleRepository.delete(talle);
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
