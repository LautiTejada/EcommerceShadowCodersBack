package com.dresscode.api_dresscode.entities;

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
    private OrdenDeCompra ordenDeCompra;

    @ManyToOne
    @JoinColumn(name = "id-producto", nullable = false)
    private Producto producto;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "precio-unitario", nullable = false)
    private Double precioUnitario;

}
