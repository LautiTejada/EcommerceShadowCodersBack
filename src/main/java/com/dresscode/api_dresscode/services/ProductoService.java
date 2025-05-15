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
@Transactional

public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;

    public List<Producto> getAllProductos() {
        return productoRepository.findAll();
    }

    public Producto getProductoById(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + id));
    }

    public Producto createProducto(Producto producto, Long categoriaId) {
        Categoria categoria = categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con id: " + categoriaId));
        producto.setCategoria(categoria);
        return productoRepository.save(producto);

    }

    public Producto updateProducto(Long id, Producto productoActualizado) {
        Producto productoExistente = getProductoById(id);

        productoExistente.setNombre(productoActualizado.getNombre());
        productoExistente.setPrecio(productoActualizado.getPrecio());
        productoExistente.setDescripcion(productoActualizado.getDescripcion());
        productoExistente.setColor(productoActualizado.getColor());
        productoExistente.setMarca(productoActualizado.getMarca());
        productoExistente.setActivo(productoActualizado.getActivo());

        if (!productoExistente.getCategoria().getId().equals(productoActualizado.getCategoria().getId())) {
            Categoria nuevaCategoria = categoriaRepository.findById(productoActualizado.getCategoria().getId())
                    .orElseThrow(() -> new RuntimeException("Categoría no encontrada con id: " + productoActualizado.getCategoria().getId()));
            productoExistente.setCategoria(nuevaCategoria);
        }

        return productoRepository.save(productoExistente);
    }

    public Producto deleteProducto(Long id) {
        Producto productoEliminado = getProductoById(id);
        productoRepository.delete(productoEliminado);
        return productoEliminado;
    }

    public Producto cambiarEstadoProducto(Long id, Boolean nuevoEstado) {
        Producto producto = getProductoById(id);
        producto.setActivo(nuevoEstado);
        return productoRepository.save(producto);
    }

    public List<Producto> getProductosByCategoria(Long categoriaId) {
        Categoria categoria = categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con id: " + categoriaId));
        return productoRepository.findByCategoria(categoria);
    }




}
