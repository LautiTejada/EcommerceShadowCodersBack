package com.dresscode.api_dresscode.services;

import com.dresscode.api_dresscode.entities.ImagenProducto;
import com.dresscode.api_dresscode.entities.Producto;
import com.dresscode.api_dresscode.repositories.ImagenProductoRepository;
import com.dresscode.api_dresscode.repositories.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor

public class ImagenProductoService {
    private final ImagenProductoRepository imagenProductoRepository;
    private final ProductoRepository productoRepository;

    // Traer todas las imágenes
    public List<ImagenProducto> getAllImagenes() {
        return imagenProductoRepository.findAll();
    }

    // Traer imagen por ID
    public ImagenProducto getImagenById(Long id) {
        return imagenProductoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Imagen no encontrada con id: " + id));
    }

    // Traer imágenes de un producto por idProducto
    public List<ImagenProducto> getImagenesByProductoId(Long productoId) {
        return imagenProductoRepository.findByProductoId(productoId);
    }

    // Crear nueva imagen para un producto
    public ImagenProducto createImagen(Long productoId, ImagenProducto imagenProducto) {
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + productoId));

        if (imagenProducto.getPrincipal()) {
            // Verificamos si ya existe una imagen principal para ese producto
            boolean existePrincipal = imagenProductoRepository.existsByProductoIdAndPrincipalTrue(productoId);
            if (existePrincipal) {
                throw new RuntimeException("Ya existe una imagen principal para este producto.");
            }
        }

        imagenProducto.setProducto(producto);
        return imagenProductoRepository.save(imagenProducto);
    }

    // Actualizar una imagen existente
    @Transactional
    public ImagenProducto updateImagen(Long id, ImagenProducto imagenActualizada) {
        ImagenProducto imagenExistente = getImagenById(id);

        if (imagenActualizada.getPrincipal() && !imagenExistente.getPrincipal()) {
            // Si se está marcando como principal y antes no lo era, validar que no exista otra
            boolean existePrincipal = imagenProductoRepository.existsByProductoIdAndPrincipalTrue(
                    imagenExistente.getProducto().getId());
            if (existePrincipal) {
                throw new RuntimeException("Ya existe una imagen principal para este producto.");
            }
        }

        imagenExistente.setUrlImagen(imagenActualizada.getUrlImagen());
        imagenExistente.setPrincipal(imagenActualizada.getPrincipal());

        return imagenProductoRepository.save(imagenExistente);
    }

    // Eliminar una imagen
    @Transactional
    public void deleteImagen(Long id) {
        ImagenProducto imagen = getImagenById(id);
        imagenProductoRepository.delete(imagen);
    }


}
