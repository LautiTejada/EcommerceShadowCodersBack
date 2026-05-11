package com.dresscode.api_dresscode.services;

import com.dresscode.api_dresscode.dtos.ImagenProductoDTO;
import com.dresscode.api_dresscode.dtos.ProductoDTO;
import com.dresscode.api_dresscode.entities.Categoria;
import com.dresscode.api_dresscode.entities.Color;
import com.dresscode.api_dresscode.entities.ImagenProducto;
import com.dresscode.api_dresscode.entities.Marca;
import com.dresscode.api_dresscode.entities.Producto;
import com.dresscode.api_dresscode.repositories.BaseRepository;
import com.dresscode.api_dresscode.repositories.CategoriaRepository;
import com.dresscode.api_dresscode.repositories.ColorRepository;
import com.dresscode.api_dresscode.repositories.ImagenProductoRepository;
import com.dresscode.api_dresscode.repositories.MarcaRepository;
import com.dresscode.api_dresscode.repositories.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
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
    private final ColorRepository colorRepository;
    private final MarcaRepository marcaRepository;

    @Override
    protected BaseRepository<Producto, Long> getRepository() {
        return productoRepository;
    }

    public List<Producto> getProductosActivos() {
        return productoRepository.findByActivoTrue();
    }

    @CacheEvict(value = "estadisticasDashboard", allEntries = true)
    public Producto createProducto(ProductoDTO producto, Long categoriaId) {
        Categoria categoria = categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con id: " + categoriaId));
        Color color = colorRepository.findByNombreColor(producto.getColor())
                .orElseThrow(() -> new RuntimeException("Color no encontrado: " + producto.getColor()));
        Marca marca = marcaRepository.findByNombreMarca(producto.getMarca())
                .orElseThrow(() -> new RuntimeException("Marca no encontrada: " + producto.getMarca()));
        Producto nuevoProducto = Producto.builder()
                .nombre(producto.getNombre())
                .precio(producto.getPrecio())
                .descripcion(producto.getDescripcion())
                .color(color)
                .marca(marca)
                .categoria(categoria)
                .build();
        nuevoProducto.setActivo(producto.getActivo() != null ? producto.getActivo() : true);
        return productoRepository.save(nuevoProducto);

    }

//    public Producto updateProducto(Long id, ProductoDTO productoActualizado) {
//        Producto productoExistente = findById(id);
//
//        productoExistente.setNombre(productoActualizado.getNombre());
//        productoExistente.setPrecio(productoActualizado.getPrecio());
//        productoExistente.setDescripcion(productoActualizado.getDescripcion());
//        productoExistente.setColor(Color.valueOf(productoActualizado.getColor().toUpperCase()));
//        productoExistente.setMarca(Marca.valueOf(productoActualizado.getMarca().toUpperCase()));
//        productoExistente.setActivo(productoActualizado.getActivo());
//
//        // Mantener descuentos y talles previos
//        productoExistente.setDescuentos(productoExistente.getDescuentos());
//        productoExistente.setTalles(productoExistente.getTalles());
//
//        return productoRepository.save(productoExistente);
//    }


    @CacheEvict(value = "estadisticasDashboard", allEntries = true)
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

        return imagenProductoRepository.save(imagenExistente);
    }

    public Producto eliminarImagenProducto(Long imagenId) {
        ImagenProducto imagen = imagenProductoRepository.findById(imagenId)
                .orElseThrow(() -> new RuntimeException("Imagen no encontrada"));
        Producto producto = imagen.getProducto();
        producto.getImagenes().remove(imagen);
        imagenProductoRepository.delete(imagen);
        return producto;
    }

    public List<ImagenProducto> getImagenesByProducto(Long productoId) {
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        return producto.getImagenes();
    }

//    public List<Producto> filtrarProductos(List<Long> tipoIds, List<Long> categoriaIds, Marca marcas, Integer precioMin, Integer precioMax) {
//        if (tipoIds != null && !tipoIds.isEmpty()) {
//            List<Long> categoriasPorTipo = new java.util.ArrayList<>(
//                    categoriaRepository.findByTipoIdIn(tipoIds)
//                            .stream()
//                            .map(Categoria::getId)
//                            .toList()
//            );
//            if (categoriaIds != null && !categoriaIds.isEmpty()) {
//                categoriasPorTipo.addAll(categoriaIds);
//            }
//            categoriaIds = categoriasPorTipo;
//        }
//        if (categoriaIds != null && categoriaIds.isEmpty()) {
//            categoriaIds = null;
//        }
//        return productoRepository.filtrarProductos(categoriaIds, marcas, precioMin, precioMax);
//    }

}
