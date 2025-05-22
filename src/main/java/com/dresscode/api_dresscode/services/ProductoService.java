package com.dresscode.api_dresscode.services;

import com.dresscode.api_dresscode.dtos.ImagenProductoDTO;
import com.dresscode.api_dresscode.dtos.ProductoDTO;
import com.dresscode.api_dresscode.entities.Categoria;
import com.dresscode.api_dresscode.entities.ImagenProducto;
import com.dresscode.api_dresscode.entities.Producto;
import com.dresscode.api_dresscode.entities.enums.Color;
import com.dresscode.api_dresscode.entities.enums.Marca;
import com.dresscode.api_dresscode.repositories.CategoriaRepository;
import com.dresscode.api_dresscode.repositories.ImagenProductoRepository;
import com.dresscode.api_dresscode.repositories.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional

public class ProductoService extends BaseServiceImpl<Producto, Long> {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final ImagenProductoRepository imagenProductoRepository;

    @Override
    protected JpaRepository<Producto, Long> getRepository() {
        return productoRepository;
    }

    public Producto createProducto(ProductoDTO producto, Long categoriaId) {
        Categoria categoria = categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con id: " + categoriaId));
        Producto nuevoProducto = Producto.builder()
                .nombre(producto.getNombre())
                .precio(producto.getPrecio())
                .descripcion(producto.getDescripcion())
                .color(Color.valueOf(producto.getColor().toUpperCase()))
                .marca(Marca.valueOf(producto.getMarca().toUpperCase()))
                .activo(producto.getActivo())
                .categoria(categoria)
                .build();
        return productoRepository.save(nuevoProducto);

    }

    public Producto updateProducto(Long id, Producto productoActualizado) {
        Producto productoExistente = findById(id);

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


    public Producto cambiarEstadoProducto(Long id, Boolean nuevoEstado) {
        Producto producto = findById(id);
        producto.setActivo(nuevoEstado);
        return productoRepository.save(producto);
    }

    public List<Producto> getProductosByCategoria(Long categoriaId) {
        Categoria categoria = categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con id: " + categoriaId));
        return productoRepository.findByCategoria(categoria);
    }

    public ImagenProducto agregarImagenAProducto(Long productoId, ImagenProducto imagen) {
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        imagen.setProducto(producto);
        return imagenProductoRepository.save(imagen);
    }

    public ImagenProducto editarImagenProducto(Long imagenId, ImagenProductoDTO imagenDTO) {
        ImagenProducto imagenExistente = imagenProductoRepository.findById(imagenId)
                .orElseThrow(() -> new RuntimeException("Imagen no encontrada"));

        imagenExistente.setUrlImagen(imagenDTO.getUrlImagen());
        imagenExistente.setPrincipal(imagenDTO.getPrincipal());

        return imagenProductoRepository.save(imagenExistente);
    }





}
