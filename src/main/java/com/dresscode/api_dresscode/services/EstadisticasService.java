package com.dresscode.api_dresscode.services;

import com.dresscode.api_dresscode.dtos.EstadisticasDTO;
import com.dresscode.api_dresscode.entities.enums.EstadoOrden;
import com.dresscode.api_dresscode.repositories.ProductoRepository;
import com.dresscode.api_dresscode.repositories.OrdenDeCompraRepository;
import com.dresscode.api_dresscode.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class EstadisticasService {
    private final ProductoRepository productoRepository;
    private final OrdenDeCompraRepository ordenDeCompraRepository;
    private final UsuarioRepository usuarioRepository;

    @Cacheable(value = "estadisticasDashboard")
    public EstadisticasDTO obtenerEstadisticas() {
        long totalProductos = productoRepository.count();
        long totalProductosActivos = productoRepository.countByActivoTrue();
        long totalOrdenes = ordenDeCompraRepository.count();
        long totalOrdenesCompletadas = ordenDeCompraRepository.countByEstadoOrden(EstadoOrden.ENTREGADO);
        long totalUsuarios = usuarioRepository.count();
        double ingresosTotales = ordenDeCompraRepository.sumIngresosTotales();
        LocalDate haceUnMes = LocalDate.now().minus(1, ChronoUnit.MONTHS);
        double ingresosUltimoMes = ordenDeCompraRepository.sumIngresosDesde(haceUnMes);
        return new EstadisticasDTO(
                totalProductos,
                totalProductosActivos,
                totalOrdenes,
                totalOrdenesCompletadas,
                totalUsuarios,
                ingresosTotales,
                ingresosUltimoMes
        );
    }
}
