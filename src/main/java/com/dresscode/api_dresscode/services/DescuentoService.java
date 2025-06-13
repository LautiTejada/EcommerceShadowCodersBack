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


    @Transactional
    public Descuento add(DescuentoDTO descuentoDTO) {
        if (descuentoDTO.getFechaCierre().isBefore(descuentoDTO.getFechaInicio())) {
            throw new RuntimeException("La fecha de cierre no puede ser anterior a la fecha de inicio.");
        }

        Descuento descuento = Descuento.builder()
                .fechaInicio(descuentoDTO.getFechaInicio())
                .fechaCierre(descuentoDTO.getFechaCierre())
                .porcentajeDescuento(descuentoDTO.getPorcentajeDescuento())
                .build();

        return descuentoRepository.save(descuento);
    }

    @Transactional
    public Descuento updateDescuento(Long id, DescuentoDTO datosActualizados) {
        Descuento descuentoExistente = findById(id);

        if (datosActualizados.getFechaCierre().isBefore(datosActualizados.getFechaInicio())) {
            throw new RuntimeException("La fecha de cierre no puede ser anterior a la fecha de inicio.");
        }

        descuentoExistente.setFechaInicio(datosActualizados.getFechaInicio());
        descuentoExistente.setFechaCierre(datosActualizados.getFechaCierre());
        descuentoExistente.setPorcentajeDescuento(datosActualizados.getPorcentajeDescuento());
        descuentoExistente.setProductos(descuentoExistente.getProductos());

        return descuentoRepository.save(descuentoExistente);
    }

    @Transactional
    public List<Producto> getProductosDeDescuento(Long descuentoId) {
        Descuento descuento = findById(descuentoId);
        return descuento.getProductos().stream()
                .map(DescuentoProducto::getProducto)
                .toList();
    }


    @Transactional
    public void agregarProductoADescuento(Long idDescuento, Long idProducto) {
        Descuento descuento = findById(idDescuento);

        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));


        boolean yaExiste = descuento.getProductos().stream()
                .anyMatch(dp -> dp.getProducto().getId().equals(idProducto));
        if (yaExiste) {
            throw new IllegalArgumentException("El producto ya tiene este descuento");
        }

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
