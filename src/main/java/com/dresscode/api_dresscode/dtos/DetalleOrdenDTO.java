package com.dresscode.api_dresscode.dtos;

import lombok.Data;

@Data
public class DetalleOrdenDTO {
    private Long productoTalleId;
    private Integer cantidad;
    private Double precioUnitario;
}
