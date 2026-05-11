package com.dresscode.api_dresscode.controllers;

import org.junit.jupiter.api.Disabled;
import com.dresscode.api_dresscode.dtos.OrdenDeCompraDTO;
import com.dresscode.api_dresscode.entities.OrdenDeCompra;
import com.dresscode.api_dresscode.services.OrdenDeCompraService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class MercadoPagoControllerTest {

    @Mock
    private com.dresscode.api_dresscode.services.OrdenDeCompraService ordenDeCompraService;

    @InjectMocks
    private MercadoPagoController mercadoPagoController;

    public MercadoPagoControllerTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @Disabled("Desactivado hasta que se mockee la API de Mercado Pago")
    void testMpEndpoint() throws Exception {
        com.dresscode.api_dresscode.dtos.OrdenDeCompraDTO dto = new com.dresscode.api_dresscode.dtos.OrdenDeCompraDTO();
        // Mock OrdenDeCompra con detalles
        com.dresscode.api_dresscode.entities.OrdenDeCompra orden = mock(com.dresscode.api_dresscode.entities.OrdenDeCompra.class);
        java.util.List<com.dresscode.api_dresscode.entities.DetalleOrden> detalles = new java.util.ArrayList<>();
        com.dresscode.api_dresscode.entities.DetalleOrden detalle = new com.dresscode.api_dresscode.entities.DetalleOrden();
        com.dresscode.api_dresscode.entities.ProductoTalle productoTalle = mock(com.dresscode.api_dresscode.entities.ProductoTalle.class);
        com.dresscode.api_dresscode.entities.Producto producto = mock(com.dresscode.api_dresscode.entities.Producto.class);
        when(productoTalle.getProducto()).thenReturn(producto);
        when(producto.getId()).thenReturn(1L);
        when(producto.getNombre()).thenReturn("Remera");
        when(producto.getDescripcion()).thenReturn("Test");
        detalle.setProductoTalle(productoTalle);
        detalle.setCantidad(1);
        detalle.setPrecioUnitario(100.0);
        detalles.add(detalle);
        when(orden.getDetalles()).thenReturn(detalles);
        when(orden.getId()).thenReturn(123L);
        when(ordenDeCompraService.crearOrdenConDetalles(any())).thenReturn(orden);
        ResponseEntity<String> response = mercadoPagoController.mp(dto);
        assertNotNull(response);
        assertTrue(response.getStatusCode().is2xxSuccessful() || response.getStatusCode().is4xxClientError());
    }
}
