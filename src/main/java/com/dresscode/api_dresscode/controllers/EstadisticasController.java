package com.dresscode.api_dresscode.controllers;

import com.dresscode.api_dresscode.dtos.EstadisticasDTO;
import com.dresscode.api_dresscode.services.EstadisticasAuditService;
import com.dresscode.api_dresscode.services.EstadisticasService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/estadisticas")
@RequiredArgsConstructor
public class EstadisticasController {
    private final EstadisticasService estadisticasService;
    private final EstadisticasAuditService estadisticasAuditService;

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EstadisticasDTO> getDashboard() {
        EstadisticasDTO dto = estadisticasService.obtenerEstadisticas();
        registrarAcceso("dashboard", "/api/estadisticas/dashboard", dto.toString());
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/dashboard/csv")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> downloadDashboardCsv() {
        EstadisticasDTO dto = estadisticasService.obtenerEstadisticas();
        String csv = "metric,value\n" +
                "totalProductos," + dto.getTotalProductos() + "\n" +
                "totalProductosActivos," + dto.getTotalProductosActivos() + "\n" +
                "totalOrdenes," + dto.getTotalOrdenes() + "\n" +
                "totalOrdenesCompletadas," + dto.getTotalOrdenesCompletadas() + "\n" +
                "totalUsuarios," + dto.getTotalUsuarios() + "\n" +
                "ingresosTotales," + dto.getIngresosTotales() + "\n" +
                "ingresosUltimoMes," + dto.getIngresosUltimoMes() + "\n";
        registrarAcceso("download_csv", "/api/estadisticas/dashboard/csv", "csv_download");
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=estadisticas_dashboard.csv")
                .contentType(new MediaType("text", "csv", StandardCharsets.UTF_8))
                .body(csv);
    }

    @GetMapping("/auditoria")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAuditoria() {
        registrarAcceso("auditoria", "/api/estadisticas/auditoria", "historial");
        return ResponseEntity.ok(estadisticasAuditService.obtenerAuditorias());
    }

    private void registrarAcceso(String accion, String endpoint, String detalles) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            estadisticasAuditService.registrarAcceso(username, accion, endpoint, detalles);
        }
    }
}
