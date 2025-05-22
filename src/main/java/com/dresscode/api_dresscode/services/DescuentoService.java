package com.dresscode.api_dresscode.services;

import com.dresscode.api_dresscode.dtos.DescuentoDTO;
import com.dresscode.api_dresscode.entities.Categoria;
import com.dresscode.api_dresscode.entities.Descuento;
import com.dresscode.api_dresscode.entities.DescuentoProducto;
import com.dresscode.api_dresscode.entities.Producto;
import com.dresscode.api_dresscode.repositories.DescuentoProductoRepository;
import com.dresscode.api_dresscode.repositories.DescuentoRepository;
import com.dresscode.api_dresscode.repositories.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor

public class DescuentoService extends BaseServiceImpl<Descuento, Long>{

    private final DescuentoRepository descuentoRepository;
    private final ProductoRepository productoRepository;

    @Override
    protected JpaRepository<Descuento, Long> getRepository() {return descuentoRepository;}

    @Override
    @Transactional
    public Descuento update(Long id, Descuento datosActualizados) {
        Descuento descuentoExistente = findById(id);

        if (datosActualizados.getFechaCierre().isBefore(datosActualizados.getFechaInicio())) {
            throw new RuntimeException("La fecha de cierre no puede ser anterior a la fecha de inicio.");
        }

        descuentoExistente.setFechaInicio(datosActualizados.getFechaInicio());
        descuentoExistente.setFechaCierre(datosActualizados.getFechaCierre());
        descuentoExistente.setPorcentajeDescuento(datosActualizados.getPorcentajeDescuento());

        return descuentoRepository.save(descuentoExistente);
    }


    @Transactional
    public void agregarProductoADescuento(Long idDescuento, Long idProducto) {
        Descuento descuento = findById(idDescuento);

        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        DescuentoProducto dp = DescuentoProducto.builder()
                .descuento(descuento)
                .producto(producto)
                .build();

        descuento.getProductos().add(dp);
        descuentoRepository.save(descuento);
    }

    @Transactional
    public void eliminarProductoDeDescuento(Long idDescuento, Long idProducto){
        Descuento descuento = findById(idDescuento);

        descuento.getProductos().removeIf(dp -> dp.getProducto().getId().equals(idProducto));
        descuentoRepository.save(descuento);
    }

}
