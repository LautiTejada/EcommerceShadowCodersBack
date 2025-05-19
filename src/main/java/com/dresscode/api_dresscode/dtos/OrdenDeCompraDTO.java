package com.dresscode.api_dresscode.dtos;

import com.dresscode.api_dresscode.entities.enums.EstadoOrden;
import com.dresscode.api_dresscode.entities.enums.MetodoPago;
import lombok.Data;

import java.util.List;

@Data
public class OrdenDeCompraDTO {
    private Long usuarioId;
    private Long direccionId;
    private Double precioTotal;
    private MetodoPago metodoPago;
    private EstadoOrden estadoOrden;
    private List<DetalleOrdenDTO> detalles;

}
