package com.dresscode.api_dresscode.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "detalle_orden")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class DetalleOrden extends Base{

    @ManyToOne
    @JoinColumn(name = "orden_id", nullable = false)
    @JsonBackReference
    private OrdenDeCompra ordenDeCompra;

    @ManyToOne
    @JoinColumn(name = "producto_talle_id", nullable = false)
    private ProductoTalle productoTalle;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "precio-unitario", nullable = false)
    private Double precioUnitario;

}
