package com.dresscode.api_dresscode.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
public class DescuentoProducto extends Base{
    @ManyToOne
    @JoinColumn(name = "descuento_id", nullable = false)
    @JsonBackReference
    private Descuento descuento;

    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    @JsonBackReference
    private Producto producto;
}
