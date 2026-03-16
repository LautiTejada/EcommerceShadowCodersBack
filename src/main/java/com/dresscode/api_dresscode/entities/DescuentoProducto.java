package com.dresscode.api_dresscode.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "descuentos_productos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class DescuentoProducto extends Base{
    @ManyToOne
    @JoinColumn(name = "descuento_id", nullable = false)
    private Descuento descuento;

    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(name = "precio_descuento", nullable = false)
    private double precioDescuento;

    public void calcularPrecioDescuento() {
        if (producto != null && descuento != null) {
            this.precioDescuento = producto.getPrecio() * (1 - descuento.getPorcentajeDescuento() / 100.0);
        }
    }
}