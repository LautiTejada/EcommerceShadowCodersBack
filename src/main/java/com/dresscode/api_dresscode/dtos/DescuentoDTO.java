package com.dresscode.api_dresscode.dtos;
import lombok.Data;
import java.time.LocalDate;

@Data
public class DescuentoDTO {
    private LocalDate fechaInicio;
    private LocalDate fechaCierre;
    private Integer porcentajeDescuento;
}
