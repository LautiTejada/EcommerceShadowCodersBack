package com.dresscode.api_dresscode.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "usuario_direccion")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class UsuarioDireccion extends Base{

    @ManyToOne
    @JoinColumn(name = "id-usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id-direccion", nullable = false)
    private Direccion direccion;

    @Column(name = "tipo", length = 20)
    private String tipoDireccion;

}
