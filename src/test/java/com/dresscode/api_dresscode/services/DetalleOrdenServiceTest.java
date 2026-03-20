package com.dresscode.api_dresscode.services;

import com.dresscode.api_dresscode.entities.DetalleOrden;
import com.dresscode.api_dresscode.repositories.DetalleOrdenRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class DetalleOrdenServiceTest {

    @Mock
    private DetalleOrdenRepository detalleOrdenRepository;
    @InjectMocks
    private DetalleOrdenService detalleOrdenService;

    public DetalleOrdenServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testActualizarDetalleOrden() {
        DetalleOrden detalle = new DetalleOrden();
        detalle.setId(1L);
        detalle.setCantidad(2);
        // Mock ProductoTalle y Producto
        com.dresscode.api_dresscode.entities.ProductoTalle productoTalle = mock(com.dresscode.api_dresscode.entities.ProductoTalle.class);
        com.dresscode.api_dresscode.entities.Producto producto = mock(com.dresscode.api_dresscode.entities.Producto.class);
        when(productoTalle.getProducto()).thenReturn(producto);
        when(producto.getPrecio()).thenReturn(100.0);
        detalle.setProductoTalle(productoTalle);
        when(detalleOrdenRepository.findById(1L)).thenReturn(java.util.Optional.of(detalle));
        when(detalleOrdenRepository.save(any())).thenReturn(detalle);
        DetalleOrden result = detalleOrdenService.actualizarDetalleOrden(1L, 5);
        assertEquals(5, result.getCantidad());
        assertEquals(100.0, result.getPrecioUnitario());
    }
}
