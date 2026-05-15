package com.dresscode.api_dresscode.services;

import com.dresscode.api_dresscode.entities.OrdenDeCompra;
import com.dresscode.api_dresscode.repositories.OrdenDeCompraRepository;
import com.dresscode.api_dresscode.entities.enums.EstadoOrden;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class OrdenDeCompraServiceTest {

    @Mock
    private OrdenDeCompraRepository ordenDeCompraRepository;

    @InjectMocks
    private OrdenDeCompraService ordenDeCompraService;

    public OrdenDeCompraServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testActualizarEstadoOrden() {
        OrdenDeCompra orden = new OrdenDeCompra();
        orden.setId(1L);
        orden.setEstadoOrden(EstadoOrden.PEDIDO);
        when(ordenDeCompraRepository.findById(1L)).thenReturn(java.util.Optional.of(orden));
        when(ordenDeCompraRepository.save(any())).thenReturn(orden);

        OrdenDeCompra result = ordenDeCompraService.actualizarEstadoOrden(1L, EstadoOrden.EN_PROCESO);
        assertEquals(EstadoOrden.EN_PROCESO, result.getEstadoOrden());
    }
}
