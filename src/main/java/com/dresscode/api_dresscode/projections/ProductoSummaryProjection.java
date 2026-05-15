package com.dresscode.api_dresscode.projections;

/**
 * Interface projection for read-only product list endpoints.
 *
 * Returns only id, nombre, and precio — avoids SELECT * on the productos table
 * and avoids JOIN loading of color, marca, categoria associations that are not
 * needed for list views.
 *
 * Spring Data JPA maps JPQL aliases to getter names automatically when using
 * interface projections. Property names here MUST match the JPQL aliases in
 * {@code ProductoRepository.findActiveSummary()}.
 */
public interface ProductoSummaryProjection {
    Long getId();
    String getNombre();
    Double getPrecio();
}
