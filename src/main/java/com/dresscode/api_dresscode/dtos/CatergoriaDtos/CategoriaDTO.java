package com.dresscode.api_dresscode.dtos.CatergoriaDtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoriaDTO {

    @NotBlank(message = "El nombre de la categoría es obligatorio")
    private String nombreCategoria;

}
