package com.dresscode.api_dresscode.entities;

import com.dresscode.api_dresscode.entities.enums.Color;
import com.dresscode.api_dresscode.entities.enums.Marca;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "productos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Producto extends Base{

    @Column(name = "nombre", nullable = false)
    @NotBlank
    @Size(min = 3, max = 100)
    private String nombre;

    @Column(name="precio", nullable = false)
    @Positive
    private Double precio;

    @Column(name = "descripcion", nullable = false)
    private String descripcion;

    @Column(name = "color", nullable = false)
    private Color color;

    @Column(name = "marca", nullable = false)
    private Marca marca;

    @Column(name = "activo")
    private Boolean activo = true;

    @ManyToOne
    @JoinColumn(name = "categoria_id", nullable = false)
    @JsonBackReference
    private Categoria categoria;

    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @JsonManagedReference
    private List<DescuentoProducto> descuentos = new ArrayList<>();

    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @JsonManagedReference
    private List<ProductoTalle> talles = new ArrayList<>();

    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ImagenProducto> imagenes = new ArrayList<>();

}
