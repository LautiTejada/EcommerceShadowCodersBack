package com.dresscode.api_dresscode.services;

import com.dresscode.api_dresscode.entities.ImagenProducto;
import com.dresscode.api_dresscode.entities.Producto;
import com.dresscode.api_dresscode.repositories.ImagenProductoRepository;
import com.dresscode.api_dresscode.repositories.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor

public class ImagenProductoService extends BaseServiceImpl<ImagenProducto, Long>{

    private final ImagenProductoRepository imagenProductoRepository;
    private final ProductoRepository productoRepository;

    @Override
    protected JpaRepository<ImagenProducto, Long> getRepository() {return imagenProductoRepository;}


    public List<ImagenProducto> getImagenesByProductoId(Long productoId) {
        return imagenProductoRepository.findByProductoId(productoId);
    }

}
