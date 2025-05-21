package com.dresscode.api_dresscode.dtos;

import com.dresscode.api_dresscode.entities.enums.Provincias;
import lombok.Data;

@Data
public class DireccionDTO {
    private Long id;
    private String calle;
    private Integer numero;
    private Integer codigoPostal;
    private String localidad;
    private Provincias provincia;
    private String pais;
}
