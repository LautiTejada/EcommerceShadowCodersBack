package com.dresscode.api_dresscode.services;

import com.dresscode.api_dresscode.dtos.OrdenDeCompraDTO;
import com.dresscode.api_dresscode.entities.*;
import com.dresscode.api_dresscode.entities.enums.EstadoOrden;
import com.dresscode.api_dresscode.repositories.DetalleOrdenRepository;
import com.dresscode.api_dresscode.repositories.DireccionRepository;
import com.dresscode.api_dresscode.repositories.OrdenDeCompraRepository;
import com.dresscode.api_dresscode.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

public class OrdenDeCompraService {

    private final OrdenDeCompraRepository ordenDeCompraRepository;
    private final UsuarioRepository usuarioRepository;
    private final DireccionRepository direccionRepository;
    private final UsuarioService usuarioService;
    private final DireccionService direccionService;
    private final ProductoTalleService productoTalleService;
    private final DetalleOrdenRepository detalleOrdenRepository;

    public List<OrdenDeCompra> getAllOrdenesDeCompra() {
        return ordenDeCompraRepository.findAll();
    }

    public OrdenDeCompra getOrdenDeCompraById(Long id) {
        return ordenDeCompraRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Orden de compra no encontrada con el id: " + id));
    }

    @Transactional
    public OrdenDeCompra createOrdenDeCompra(OrdenDeCompra ordenDeCompra) {

        var usuario = usuarioService.getUsuarioById(ordenDeCompra.getUsuario().getId());
        ordenDeCompra.setUsuario(usuario);

        var direccion = direccionService.getDireccionById(ordenDeCompra.getDireccion().getId());
        ordenDeCompra.setDireccion(direccion);

        ordenDeCompra.setUsuario(usuario);
        ordenDeCompra.setDireccion(direccion);
        ordenDeCompra.setFecha(LocalDate.now());
        ordenDeCompra.setEstadoOrden(EstadoOrden.PEDIDO);
        return ordenDeCompraRepository.save(ordenDeCompra);
    }



    @Transactional
    public OrdenDeCompra actualizarEstadoOrden(Long ordenId, EstadoOrden nuevoEstado) {
        OrdenDeCompra orden = getOrdenDeCompraById(ordenId);
        orden.setEstadoOrden(nuevoEstado);
        return ordenDeCompraRepository.save(orden);
    }

    @Transactional
    public void eliminarOrden(Long id) {
        OrdenDeCompra orden = getOrdenDeCompraById(id);
        ordenDeCompraRepository.delete(orden);
    }

    public List<OrdenDeCompra> getOrdenesByUsuario(Long usuarioId) {
        return ordenDeCompraRepository.findByUsuarioId(usuarioId);
    }

    @Transactional
    public OrdenDeCompra crearOrdenConDetalles(OrdenDeCompraDTO ordenCompra) {
        Usuario usuario = usuarioRepository.findById(ordenCompra.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Direccion direccion = direccionRepository.findById(ordenCompra.getDireccionId())
                .orElseThrow(() -> new RuntimeException("Dirección no encontrada"));

        // Calcular precio total de los detalles
        Double precioTotal = ordenCompra.getDetalles().stream()
                .mapToDouble(detalle -> detalle.getCantidad() * detalle.getPrecioUnitario())
                .sum();

        OrdenDeCompra orden = OrdenDeCompra.builder()
                .usuario(usuario)
                .direccion(direccion)
                .fecha(LocalDate.now())
                .precioTotal(precioTotal)
                .metodoPago(ordenCompra.getMetodoPago())
                .estadoOrden(ordenCompra.getEstadoOrden())
                .build();

        ordenDeCompraRepository.save(orden);

        List<DetalleOrden> detalles = ordenCompra.getDetalles().stream().map(detalleReq -> {
            ProductoTalle productoTalle = productoTalleService.traerProductoTallePorId(detalleReq.getProductoTalleId());


            return DetalleOrden.builder()
                    .ordenDeCompra(orden)
                    .productoTalle(productoTalle)
                    .cantidad(detalleReq.getCantidad())
                    .precioUnitario(detalleReq.getPrecioUnitario())
                    .build();
        }).collect(Collectors.toList());

        detalleOrdenRepository.saveAll(detalles);
        orden.setDetalles(detalles);

        return orden;
    }



}
