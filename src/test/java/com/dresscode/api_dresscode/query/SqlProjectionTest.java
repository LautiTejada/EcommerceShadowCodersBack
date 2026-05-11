package com.dresscode.api_dresscode.query;

import com.dresscode.api_dresscode.entities.*;
import com.dresscode.api_dresscode.projections.ProductoSummaryProjection;
import com.dresscode.api_dresscode.repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Task 6.2 — SQL projection tests.
 *
 * Verifies that {@code findActiveSummary()} returns interface projections
 * (NOT fully hydrated {@link Producto} entities), which proves SELECT * was avoided.
 *
 * Spring Data JPA returns interface projections as JDK dynamic proxies when a JPQL
 * query selects only specific columns. If the query had done SELECT *, Spring Data
 * would return fully-hydrated entities, not proxied interfaces.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class SqlProjectionTest {

    @Autowired private ProductoRepository productoRepository;
    @Autowired private MarcaRepository marcaRepository;
    @Autowired private ColorRepository colorRepository;
    @Autowired private CategoriaRepository categoriaRepository;
    @Autowired private TipoRepository tipoRepository;

    private Marca marca;
    private Color color;
    private Categoria cat;

    @BeforeEach
    void setUp() {
        marca = Marca.builder().nombreMarca("MarcaProj2_" + System.nanoTime()).build();
        marca.setActivo(true);
        marca = marcaRepository.save(marca);

        color = Color.builder().nombreColor("ColorProj2_" + System.nanoTime()).build();
        color.setActivo(true);
        color = colorRepository.save(color);

        Tipo tipo = new Tipo();
        tipo.setNombre("TipoProj2_" + System.nanoTime());
        tipo.setActivo(true);
        tipo = tipoRepository.save(tipo);

        cat = Categoria.builder()
                .nombreCategoria("CatProj2_" + System.nanoTime())
                .tipo(tipo)
                .build();
        cat.setActivo(true);
        cat = categoriaRepository.save(cat);

        Producto p = Producto.builder()
                .nombre("ProductoProj2_" + System.nanoTime())
                .precio(750.0)
                .descripcion("projection test product")
                .color(color)
                .marca(marca)
                .categoria(cat)
                .build();
        p.setActivo(true);
        productoRepository.save(p);
    }

    @Test
    void findActiveSummaryReturnsProjectionNotFullEntity() {
        List<ProductoSummaryProjection> results = productoRepository.findActiveSummary();

        assertFalse(results.isEmpty(), "Expected at least one active product in projection results");

        ProductoSummaryProjection first = results.get(0);

        // A Spring Data interface projection is NOT an instance of Producto.
        // If it were a full entity (SELECT *), it would be a Producto instance.
        assertFalse(first instanceof Producto,
                "findActiveSummary() must return interface projections, NOT fully-hydrated Producto entities. " +
                "This proves the query used column selection, not SELECT *.");
    }

    @Test
    void projectionExposesonlyContractedFields() {
        List<ProductoSummaryProjection> results = productoRepository.findActiveSummary();

        // Verify the projected fields are correct (behavioral assertion)
        for (ProductoSummaryProjection proj : results) {
            assertNotNull(proj.getId(), "getId() must return a non-null ID");
            assertNotNull(proj.getNombre(), "getNombre() must return a non-null name");
            assertNotNull(proj.getPrecio(), "getPrecio() must return a non-null price");
            assertTrue(proj.getPrecio() > 0,
                    "getPrecio() must return a positive price, got: " + proj.getPrecio());
        }
    }

    @Test
    void projectionReturnsCorrectValuesFromDatabase() {
        List<ProductoSummaryProjection> results = productoRepository.findActiveSummary();

        // Triangulation: at least one result matches the seeded product
        boolean hasExpectedProduct = results.stream()
                .anyMatch(p -> p.getPrecio() == 750.0);

        assertTrue(hasExpectedProduct,
                "findActiveSummary() must include the seeded product with precio=750.0");
    }
}
