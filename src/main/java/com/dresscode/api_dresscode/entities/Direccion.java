package com.dresscode.api_dresscode.entities;

import com.dresscode.api_dresscode.entities.enums.Provincias;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "direccion")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Direccion extends Base{

    @Column(name = "calle", nullable = false)
    private String calle;

    @Column(name = "numero", nullable = false)
    private Integer numero;

    @Column(name = "codigo_postal", nullable = false)
    private Integer codigoPostal;

    @Column(name = "localidad", nullable = false)
    private String localidad;

    @Column(name = "provincia", nullable = false)
    private Provincias provincia;

    @Column(name = "pais", nullable = false)
    private String pais = "Argentina";


    @OneToMany(mappedBy = "direccion", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<UsuarioDireccion> usuarios = new HashSet<>();
}
