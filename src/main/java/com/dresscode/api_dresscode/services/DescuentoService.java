package com.dresscode.api_dresscode.services;

import com.dresscode.api_dresscode.dtos.DescuentoUpdateDTO;
import com.dresscode.api_dresscode.entities.Descuento;
import com.dresscode.api_dresscode.entities.DescuentoProducto;
import com.dresscode.api_dresscode.entities.Producto;
import com.dresscode.api_dresscode.repositories.DescuentoProductoRepository;
import com.dresscode.api_dresscode.repositories.DescuentoRepository;
import com.dresscode.api_dresscode.repositories.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor

public class DescuentoService {

    private final DescuentoRepository descuentoRepository;
    private final ProductoRepository productoRepository;
    private final DescuentoProductoRepository descuentoProductoRepository;

    public List<Descuento> getAllDescuentos() {
        return descuentoRepository.findAll();
    }

    public Descuento getDescuentoById(Long id) {
        return descuentoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Descuento no encontrado con id: "+ id));
    }

    public Descuento createDescuento(Descuento descuento) {
        return descuentoRepository.save(descuento);
    }

    public void deleteDescuento(Long id) {
        Descuento descuentoEliminado = getDescuentoById(id);
        if (descuentoEliminado == null) {
            throw new RuntimeException("Descuento no encontrado con id: " + id);
        }
        descuentoRepository.delete(descuentoEliminado);
    }

    @Transactional
    public Descuento editarDescuento(Long id, DescuentoUpdateDTO datosActualizados) {
        Descuento descuentoExistente = getDescuentoById(id);

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
        Descuento descuento = getDescuentoById(idDescuento);

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
        Descuento descuento = getDescuentoById(idDescuento);

        descuento.getProductos().removeIf(dp -> dp.getProducto().getId().equals(idProducto));
        descuentoRepository.save(descuento);
    }

}
