package com.dresscode.api_dresscode.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para crear/actualizar banners
 * Acepta marcaId como Long en lugar del objeto Marca completo
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BannerDTO {
    
    @NotBlank(message = "El título es requerido")
    private String titulo;
    
    @NotBlank(message = "El nombre de la imagen es requerido")
    private String imagenNombre;
    
    @NotNull(message = "La marca es requerida")
    private Long marcaId;
    
    @NotNull(message = "El orden es requerido")
    @Positive(message = "El orden debe ser un número positivo")
    private Integer orden;
    
    private Boolean activo;
}
