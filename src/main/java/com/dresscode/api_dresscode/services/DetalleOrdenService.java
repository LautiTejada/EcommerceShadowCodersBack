package com.dresscode.api_dresscode.services;

import com.dresscode.api_dresscode.entities.DetalleOrden;
import com.dresscode.api_dresscode.entities.OrdenDeCompra;
import com.dresscode.api_dresscode.entities.Producto;
import com.dresscode.api_dresscode.repositories.DetalleOrdenRepository;
import com.dresscode.api_dresscode.repositories.OrdenDeCompraRepository;
import com.dresscode.api_dresscode.repositories.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor

public class DetalleOrdenService {

    private final DetalleOrdenRepository detalleOrdenRepository;
    private final OrdenDeCompraRepository ordenDeCompraRepository;
    private final ProductoRepository productoRepository;
    private final OrdenDeCompraService ordenDeCompraService;
    private final ProductoService productoService;

    public List<DetalleOrden> getAllDetalles() {
        return detalleOrdenRepository.findAll();
    }

    public DetalleOrden getDetalleById(Long id) {
        return detalleOrdenRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Detalle no encontrado con id: " + id));
    }


    public List<DetalleOrden> obtenerDetallesPorOrden(Long ordenId) {
        return detalleOrdenRepository.findByOrdenDeCompraId(ordenId);
    }


    @Transactional
    public void eliminarDetalleDeOrden(Long detalleId) {
        DetalleOrden detalleOrden = getDetalleById(detalleId);

        detalleOrdenRepository.delete(detalleOrden);
    }

    @Transactional
    public DetalleOrden actualizarDetalleOrden(Long detalleId, Integer nuevaCantidad) {
        DetalleOrden detalleOrden = getDetalleById(detalleId);

        if (nuevaCantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0.");
        }

        detalleOrden.setCantidad(nuevaCantidad);
        Double precioUnitario = detalleOrden.getProductoTalle().getProducto().getPrecio();
        detalleOrden.setPrecioUnitario(precioUnitario);

        return detalleOrdenRepository.save(detalleOrden);
    }

}
