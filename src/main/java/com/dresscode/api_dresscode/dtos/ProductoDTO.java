package com.dresscode.api_dresscode.dtos;

import lombok.Data;

@Data
public class ProductoDTO {
    private String nombre;
    private Double precio;
    private String descripcion;
    private String color; // tipo String para mapear después al enum
    private String marca;
    private Boolean activo;
}
