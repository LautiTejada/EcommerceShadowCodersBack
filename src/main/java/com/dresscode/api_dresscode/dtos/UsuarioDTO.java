package com.dresscode.api_dresscode.dtos;

import com.dresscode.api_dresscode.entities.enums.Rol;
import lombok.Data;

import java.util.List;

@Data
public class UsuarioDTO {
    private Long id;
    private String nombreUsuario;
    private String email;
    private Rol rol;
    private List<DireccionDTO> direcciones;
}
