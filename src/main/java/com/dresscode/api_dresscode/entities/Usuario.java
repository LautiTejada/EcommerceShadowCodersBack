package com.dresscode.api_dresscode.entities;

import com.dresscode.api_dresscode.entities.enums.Rol;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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

    @Column(nullable = false, name = "contrasena", length = 50)
    private String contrasena;

    @Column(nullable = false, name = "rol")
    @Builder.Default
    private Rol rol = Rol.USER;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @JsonManagedReference
    private List<Direccion> direcciones= new ArrayList<>();

}
