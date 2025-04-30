package com.dresscode.api_dresscode.entities;

import com.dresscode.api_dresscode.entities.enums.EstadoOrden;
import com.dresscode.api_dresscode.entities.enums.MetodoPago;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ordenes_de_compra")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class OrdenDeCompra extends Base{

    @ManyToOne
    @JoinColumn(name = "id-usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id-direccion", nullable = false)
    private Direccion direccion;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Column(name = "precio_total", nullable = false)
    private Double precioTotal;

    @Enumerated(EnumType.STRING)
    @Column(name = "metodo-pago", nullable = false)
    private MetodoPago metodoPago;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoOrden estadoOrden;

    @OneToMany(mappedBy = "orden", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DetalleOrden> detalles = new ArrayList<>();


}
