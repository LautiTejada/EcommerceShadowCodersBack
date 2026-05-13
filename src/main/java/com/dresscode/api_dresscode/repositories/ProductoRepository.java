package com.dresscode.api_dresscode.repositories;

import com.dresscode.api_dresscode.entities.Categoria;
import com.dresscode.api_dresscode.entities.Marca;
import com.dresscode.api_dresscode.entities.Producto;
import com.dresscode.api_dresscode.projections.ProductoSummaryProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends BaseRepository<Producto, Long> {
    List<Producto> findByCategoria(Categoria categoria);
    List<Producto> findByActivoTrue();
    Page<Producto> findByActivoTrue(Pageable pageable);
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

    /**
     * Unified paginated filter. Each dimension is optional:
     * pass null or empty list to skip that filter.
     */
    @Query("SELECT p FROM Producto p WHERE p.activo = true " +
           "AND (:#{#tipoIds == null || #tipoIds.isEmpty()} = true OR p.categoria.tipo.id IN :tipoIds) " +
           "AND (:#{#categoriaIds == null || #categoriaIds.isEmpty()} = true OR p.categoria.id IN :categoriaIds) " +
           "AND (:#{#marcaIds == null || #marcaIds.isEmpty()} = true OR p.marca.id IN :marcaIds)")
    Page<Producto> findActivosFiltrados(
            @Param("tipoIds") List<Long> tipoIds,
            @Param("categoriaIds") List<Long> categoriaIds,
            @Param("marcaIds") List<Long> marcaIds,
            Pageable pageable
    );
}
