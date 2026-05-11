package com.dresscode.api_dresscode.repositories;

import com.dresscode.api_dresscode.entities.Categoria;
import com.dresscode.api_dresscode.entities.Marca;
import com.dresscode.api_dresscode.entities.Producto;
import com.dresscode.api_dresscode.projections.ProductoSummaryProjection;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends BaseRepository<Producto, Long> {
    List<Producto> findByCategoria(Categoria categoria);
    List<Producto> findByActivoTrue();
    long countByActivoTrue();

    /**
     * Returns a lightweight summary projection for active products.
     * Selects only id, nombre, and precio — no SELECT * on the productos table.
     * Use this for public-facing read-only list endpoints instead of {@link #findByActivoTrue()}.
     */
    @Query("SELECT p.id AS id, p.nombre AS nombre, p.precio AS precio FROM Producto p WHERE p.activo = true")
    List<ProductoSummaryProjection> findActiveSummary();

    @Query("SELECT p FROM Producto p WHERE " +
            "p.activo = true AND " +
            "(:categoriaIds IS NULL OR p.categoria.id IN :categoriaIds) AND " +
            "(:marcas IS NULL OR p.marca IN :marcas) AND " +
            "(:precioMin IS NULL OR p.precio >= :precioMin) AND " +
            "(:precioMax IS NULL OR p.precio <= :precioMax)")
    List<Producto> filtrarProductos(
            @Param("categoriaIds") List<Long> categoriaIds,
            @Param("marcas") Marca marcas,
            @Param("precioMin") Integer precioMin,
            @Param("precioMax") Integer precioMax
    );
}
