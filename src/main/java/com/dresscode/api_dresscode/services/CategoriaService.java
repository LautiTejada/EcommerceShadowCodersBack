package com.dresscode.api_dresscode.services;

import com.dresscode.api_dresscode.entities.Categoria;
import com.dresscode.api_dresscode.entities.Producto;
import com.dresscode.api_dresscode.repositories.CategoriaRepository;
import com.dresscode.api_dresscode.repositories.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor

public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;


    public List<Categoria> getAllCategorias() {
        return categoriaRepository.findAll();
    }

    public Categoria getCategoriaById(Long id) {
        return categoriaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Categoria no encontrada con id: "+ id));
    }

    public Categoria createCategoria(Categoria categoria) {
        return categoriaRepository.save(categoria);
    }

    @Transactional
    public Categoria updateCategoria(Long id, Categoria categoriaActuializada) {
        Categoria categoriaExistente = getCategoriaById(id);
        categoriaExistente.setNombreCategoria(categoriaActuializada.getNombreCategoria());
        categoriaExistente.setTipo(categoriaActuializada.getTipo());
        return categoriaRepository.save(categoriaExistente);
    }

    @Transactional
    public void deleteCategoria(Long id) {
        Categoria categoriaEliminada = getCategoriaById(id);
        if (!categoriaEliminada.getProductos().isEmpty()) {
            throw new RuntimeException("No se puede eliminar la categoría porque tiene productos asociados.");
        }
        categoriaRepository.delete(categoriaEliminada);
    }

    public List<Producto> getProductosByCategoria(Long categoriaId) {
        Categoria categoria = getCategoriaById(categoriaId); // Para validar existencia
        return productoRepository.findByCategoriaId(categoriaId);
    }

}
