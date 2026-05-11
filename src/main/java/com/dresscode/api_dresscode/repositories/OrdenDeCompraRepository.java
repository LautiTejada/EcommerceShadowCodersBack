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
}
