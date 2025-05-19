package com.dresscode.api_dresscode.dtos;
import lombok.Data;
import java.time.LocalDate;

@Data
public class DescuentoUpdateDTO {
    private LocalDate fechaInicio;
    private LocalDate fechaCierre;
    private Integer porcentajeDescuento;
}
