package com.dresscode.api_dresscode.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder



public class Usuario extends Base{

    @Column(nullable = false, name = "nombre-usuario", length = 100)
    private String nombreUsuario;

    @Column(nullable = false, name = "email", unique = true, length = 50)
    private String email;

    @Column(nullable = false, name = "contrasena", length = 100)
    private String contrasena;

    @Column(nullable = false, name = "rol")
    @Builder.Default
    private boolean rol = false;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<UsuarioDireccion> direcciones = new HashSet<>();

}
