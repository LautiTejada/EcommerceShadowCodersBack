package com.dresscode.api_dresscode.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FavoritoDTO {
    private Long id;
    private Long usuarioId;
    private Long productoId;
    private String nombreProducto;
    private Double precioProducto;
    private LocalDateTime fechaAgregado;
}
