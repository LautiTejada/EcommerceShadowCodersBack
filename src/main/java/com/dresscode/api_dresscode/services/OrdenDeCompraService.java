package com.dresscode.api_dresscode.services;

import com.dresscode.api_dresscode.dtos.OrdenDeCompraDTO;
import com.dresscode.api_dresscode.entities.*;
import com.dresscode.api_dresscode.entities.enums.EstadoOrden;
import com.dresscode.api_dresscode.entities.enums.MetodoPago;
import com.dresscode.api_dresscode.repositories.BaseRepository;
import com.dresscode.api_dresscode.repositories.DetalleOrdenRepository;
import com.dresscode.api_dresscode.repositories.DireccionRepository;
import com.dresscode.api_dresscode.repositories.OrdenDeCompraRepository;
import com.dresscode.api_dresscode.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import com.mercadopago.resources.payment.Payment;

@Service
@RequiredArgsConstructor

public class OrdenDeCompraService extends BaseServiceImpl<OrdenDeCompra, Long> {

    private final OrdenDeCompraRepository ordenDeCompraRepository;
    private final UsuarioRepository usuarioRepository;
    private final DireccionRepository direccionRepository;
    private final UsuarioService usuarioService;
    private final DireccionService direccionService;
    private final ProductoTalleService productoTalleService;
    private final DetalleOrdenRepository detalleOrdenRepository;
    private final EmailService emailService;

    @Override
    protected BaseRepository<OrdenDeCompra, Long> getRepository() {
        return ordenDeCompraRepository;
    }



    @Transactional
    @CacheEvict(value = "estadisticasDashboard", allEntries = true)
    public OrdenDeCompra actualizarEstadoOrden(Long ordenId, EstadoOrden nuevoEstado) {
        OrdenDeCompra orden = findById(ordenId);
        orden.setEstadoOrden(nuevoEstado);
        OrdenDeCompra guardada = ordenDeCompraRepository.save(orden);
        // TODO: habilitar cuando tengamos email configurado
        // emailService.enviarCambioEstadoOrden(guardada);
        return guardada;
    }

    // Actualiza el estado de la orden según el pago de Mercado Pago
    @Transactional
    @CacheEvict(value = "estadisticasDashboard", allEntries = true)
    public void actualizarEstadoPorPago(Payment payment) {
        // Suponiendo que el external_reference es el id de la orden
        String externalReference = payment.getExternalReference();
        if (externalReference == null) return;
        Long ordenId;
        try {
            ordenId = Long.parseLong(externalReference);
        } catch (NumberFormatException e) {
            return;
        }
        var orden = ordenDeCompraRepository.findById(ordenId);
        if (orden.isEmpty()) return;
        var estado = switch (payment.getStatus()) {
            case "approved" -> EstadoOrden.ENTREGADO;
            case "pending" -> EstadoOrden.EN_PROCESO;
            case "in_process" -> EstadoOrden.EN_PROCESO;
            case "rejected" -> EstadoOrden.PEDIDO;
            default -> EstadoOrden.PEDIDO;
        };
        OrdenDeCompra o = orden.get();
        o.setEstadoOrden(estado);
        OrdenDeCompra guardada = ordenDeCompraRepository.save(o);
        // TODO: habilitar cuando tengamos email configurado
        // emailService.enviarCambioEstadoOrden(guardada);
    }


    public List<OrdenDeCompra> getOrdenesByUsuario(Long usuarioId) {
        return ordenDeCompraRepository.findByUsuarioId(usuarioId);
    }

    @Transactional
    @CacheEvict(value = "estadisticasDashboard", allEntries = true)
    public OrdenDeCompra crearOrdenConDetalles(OrdenDeCompraDTO ordenCompra) {
        Usuario usuario = usuarioRepository.findById(ordenCompra.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Si no se proporciona dirección, usar la primera dirección activa del usuario
        // Uses DB-level WHERE clause via findFirstByUsuarioIdAndActivoTrue — avoids loading all
        // addresses into memory and filtering in Java (in-memory filter anti-pattern).
        Direccion direccion;
        if (ordenCompra.getDireccionId() != null) {
            direccion = direccionRepository.findById(ordenCompra.getDireccionId())
                    .orElseThrow(() -> new RuntimeException("Dirección no encontrada"));
        } else {
            direccion = direccionRepository.findFirstByUsuarioIdAndActivoTrue(ordenCompra.getUsuarioId())
                    .orElseThrow(() -> new RuntimeException("Usuario sin dirección activa"));
        }

        // Calcular precio total de los detalles si no se proporciona
        Double precioTotal = ordenCompra.getPrecioTotal();
        if (precioTotal == null || precioTotal == 0) {
            precioTotal = ordenCompra.getDetalles().stream()
                    .mapToDouble(detalle -> detalle.getCantidad() * detalle.getPrecioUnitario())
                    .sum();
        }

        // Establecer método de pago por defecto si no se proporciona
        MetodoPago metodoPago = ordenCompra.getMetodoPago() != null ? ordenCompra.getMetodoPago() : MetodoPago.CREDITO;

        OrdenDeCompra orden = OrdenDeCompra.builder()
                .usuario(usuario)
                .direccion(direccion)
                .fecha(LocalDate.now())
                .precioTotal(precioTotal)
                .metodoPago(metodoPago)
                .estadoOrden(ordenCompra.getEstadoOrden() != null ? ordenCompra.getEstadoOrden() : EstadoOrden.PEDIDO)
                .build();
        orden.setActivo(true);
        ordenDeCompraRepository.save(orden);

        List<DetalleOrden> detalles = ordenCompra.getDetalles().stream().map(detalleReq -> {
            ProductoTalle productoTalle = productoTalleService.findById(detalleReq.getProductoTalleId());


            return DetalleOrden.builder()
                    .ordenDeCompra(orden)
                    .productoTalle(productoTalle)
                    .cantidad(detalleReq.getCantidad())
                    .precioUnitario(detalleReq.getPrecioUnitario())
                    .build();
        }).collect(Collectors.toList());

        detalleOrdenRepository.saveAll(detalles);
        orden.setDetalles(detalles);

        // TODO: habilitar cuando tengamos email configurado
        // emailService.enviarConfirmacionOrden(orden);
        return orden;
    }



}
