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
import java.time.Month;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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

    public List<Map<String, Object>> ventasPorMes(int meses) {
        LocalDate desde = LocalDate.now().minus(meses, ChronoUnit.MONTHS).withDayOfMonth(1);
        List<Object[]> rows = ordenDeCompraRepository.ventasPorMes(desde);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object[] row : rows) {
            int anio = ((Number) row[0]).intValue();
            int mes = ((Number) row[1]).intValue();
            double total = ((Number) row[2]).doubleValue();
            String label = Month.of(mes).getDisplayName(TextStyle.SHORT, new Locale("es", "AR"))
                    + " " + anio;
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("mes", label);
            entry.put("total", total);
            result.add(entry);
        }
        return result;
    }

    public List<Map<String, Object>> topProductos(int limit) {
        List<Object[]> rows = ordenDeCompraRepository.topProductos();
        List<Map<String, Object>> result = new ArrayList<>();
        int count = 0;
        for (Object[] row : rows) {
            if (count++ >= limit) break;
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("id", row[0]);
            entry.put("nombre", row[1]);
            entry.put("unidades", ((Number) row[2]).longValue());
            entry.put("ingresos", ((Number) row[3]).doubleValue());
            result.add(entry);
        }
        return result;
    }
}
