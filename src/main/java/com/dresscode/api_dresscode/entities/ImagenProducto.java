package com.dresscode.api_dresscode.entities;

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

public class ImagenProducto extends Base {

    @Column(name = "url-imagen", nullable = false)
    private String urlImagen;

    @Column(name = "principal", nullable = false)
    private Boolean principal;

    @ManyToOne
    @JoinColumn(name = "id-producto", nullable = false)
    private Producto producto;
}
