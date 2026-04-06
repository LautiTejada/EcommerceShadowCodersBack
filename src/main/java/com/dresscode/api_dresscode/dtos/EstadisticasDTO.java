package com.dresscode.api_dresscode.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstadisticasDTO {
    private long totalProductos;
    private long totalProductosActivos;
    private long totalOrdenes;
    private long totalOrdenesCompletadas;
    private long totalUsuarios;
    private double ingresosTotales;
    private double ingresosUltimoMes;
}
