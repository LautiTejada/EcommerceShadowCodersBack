package com.dresscode.api_dresscode.dtos;

import lombok.Data;

@Data
public class ProductoTalleRequest {
    private Long productoId;
    private Long talleId;
    private Integer cantidad;
}
