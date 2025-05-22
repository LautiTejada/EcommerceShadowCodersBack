package com.dresscode.api_dresscode.services;

import com.dresscode.api_dresscode.entities.DetalleOrden;
import com.dresscode.api_dresscode.entities.OrdenDeCompra;
import com.dresscode.api_dresscode.entities.Producto;
import com.dresscode.api_dresscode.repositories.DetalleOrdenRepository;
import com.dresscode.api_dresscode.repositories.OrdenDeCompraRepository;
import com.dresscode.api_dresscode.repositories.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor

public class DetalleOrdenService extends BaseServiceImpl<DetalleOrden, Long>{

    private final DetalleOrdenRepository detalleOrdenRepository;

    @Override
    protected JpaRepository<DetalleOrden, Long> getRepository(){return detalleOrdenRepository;}


    public List<DetalleOrden> obtenerDetallesPorOrden(Long ordenId) {
        return detalleOrdenRepository.findByOrdenDeCompraId(ordenId);
    }


    @Transactional
    public DetalleOrden actualizarDetalleOrden(Long detalleId, Integer nuevaCantidad) {
        DetalleOrden detalleOrden = findById(detalleId);

        if (nuevaCantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0.");
        }

        detalleOrden.setCantidad(nuevaCantidad);
        Double precioUnitario = detalleOrden.getProductoTalle().getProducto().getPrecio();
        detalleOrden.setPrecioUnitario(precioUnitario);

        return detalleOrdenRepository.save(detalleOrden);
    }

}
