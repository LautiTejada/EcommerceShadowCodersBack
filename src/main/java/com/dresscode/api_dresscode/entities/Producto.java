package com.dresscode.api_dresscode.entities;

import com.dresscode.api_dresscode.entities.enums.Color;
import com.dresscode.api_dresscode.entities.enums.Marca;
import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

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
    private Categoria categoria;

    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DescuentoProducto> descuentos = new ArrayList<>();

    // Relación ManyToMany con Talle (tabla intermedia Calle-producto)
    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProductoTalle> talles = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ImagenProducto> imagenes = new ArrayList<>();

}
