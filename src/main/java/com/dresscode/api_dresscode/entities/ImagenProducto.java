package com.dresscode.api_dresscode.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "imagen_producto")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class ImagenProducto extends Base {

    @Column(name = "url-imagen", nullable = false)
    private String urlImagen;

    @Column(name = "principal", nullable = false)
    private Boolean principal;

    @ManyToOne
    @JoinColumn(name = "id-producto", nullable = false)
    @JsonIgnore
    private Producto producto;
}
