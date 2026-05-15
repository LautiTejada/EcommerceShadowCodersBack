package com.dresscode.api_dresscode.repositories;

import com.dresscode.api_dresscode.entities.OrdenDeCompra;
import com.dresscode.api_dresscode.entities.enums.EstadoOrden;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OrdenDeCompraRepository extends BaseRepository<OrdenDeCompra, Long> {
    List<OrdenDeCompra> findByUsuarioId(Long usuarioId);
    long countByEstadoOrden(EstadoOrden estadoOrden);

    @Query("SELECT COALESCE(SUM(o.precioTotal),0) FROM OrdenDeCompra o WHERE o.estadoOrden = com.dresscode.api_dresscode.entities.enums.EstadoOrden.ENTREGADO")
    double sumIngresosTotales();

    @Query("SELECT COALESCE(SUM(o.precioTotal),0) FROM OrdenDeCompra o WHERE o.estadoOrden = com.dresscode.api_dresscode.entities.enums.EstadoOrden.ENTREGADO AND o.fecha >= :desde")
    double sumIngresosDesde(@Param("desde") LocalDate desde);

    /** Ventas agrupadas por mes (últimos N meses). Devuelve [año, mes, totalVentas]. */
    @Query("""
        SELECT EXTRACT(YEAR FROM o.fecha), EXTRACT(MONTH FROM o.fecha), COALESCE(SUM(o.precioTotal), 0)
        FROM OrdenDeCompra o
        WHERE o.estadoOrden = com.dresscode.api_dresscode.entities.enums.EstadoOrden.ENTREGADO
          AND o.fecha >= :desde
        GROUP BY EXTRACT(YEAR FROM o.fecha), EXTRACT(MONTH FROM o.fecha)
        ORDER BY EXTRACT(YEAR FROM o.fecha), EXTRACT(MONTH FROM o.fecha)
        """)
    List<Object[]> ventasPorMes(@Param("desde") LocalDate desde);

    /** Top productos más vendidos por cantidad. Devuelve [productoId, nombreProducto, totalUnidades, totalIngresos]. */
    @Query("""
        SELECT d.productoTalle.producto.id, d.productoTalle.producto.nombre,
               SUM(d.cantidad), SUM(d.precioUnitario * d.cantidad)
        FROM DetalleOrden d
        WHERE d.ordenDeCompra.estadoOrden = com.dresscode.api_dresscode.entities.enums.EstadoOrden.ENTREGADO
        GROUP BY d.productoTalle.producto.id, d.productoTalle.producto.nombre
        ORDER BY SUM(d.cantidad) DESC
        """)
    List<Object[]> topProductos();
}
