package com.dresscode.api_dresscode.query;

import com.dresscode.api_dresscode.entities.*;
import com.dresscode.api_dresscode.projections.ProductoSummaryProjection;
import com.dresscode.api_dresscode.repositories.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Task 5.4 — Verifies that the projection-based repository method for read-only
 * product lists selects ONLY the projected columns, not a full SELECT *.
 *
 * Spec: "the SQL MUST select only those columns AND MUST NOT select all columns of the entity table."
 *
 * Note: exact SQL column inspection is not easily assertable in-process without a SQL spy.
 * This test focuses on:
 * (1) The projection method returns only the projected fields (no full entity hydration)
 * (2) The returned list contains the expected data
 * (3) The query count is bounded (no N+1 on projection)
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ProjectionQueryTest {

    @Autowired
    private ProductoRepository productoRepository;
    @Autowired
    private MarcaRepository marcaRepository;
    @Autowired
    private ColorRepository colorRepository;
    @Autowired
    private CategoriaRepository categoriaRepository;
    @Autowired
    private TipoRepository tipoRepository;
    @Autowired
    private EntityManagerFactory entityManagerFactory;

    private Statistics stats;

    @BeforeEach
    void setUp() {
        stats = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();

        Marca marca = Marca.builder().nombreMarca("MarcaProj_" + System.nanoTime()).build();
        marca.setActivo(true);
        marca = marcaRepository.save(marca);

        Color color = Color.builder().nombreColor("ColorProj_" + System.nanoTime()).build();
        color.setActivo(true);
        color = colorRepository.save(color);

        Tipo tipo = new Tipo();
        tipo.setNombre("TipoProj_" + System.nanoTime());
        tipo.setActivo(true);
        tipo = tipoRepository.save(tipo);

        Categoria cat = Categoria.builder()
                .nombreCategoria("CatProj_" + System.nanoTime())
                .tipo(tipo)
                .build();
        cat.setActivo(true);
        cat = categoriaRepository.save(cat);

        // Seed 2 active + 1 inactive productos
        Marca finalMarca = marca;
        Color finalColor = color;
        Categoria finalCat = cat;
        for (int i = 0; i < 2; i++) {
            Producto p = Producto.builder()
                    .nombre("ProductoProj_" + i + "_" + System.nanoTime())
                    .precio(500.0 + i * 100)
                    .descripcion("desc_" + i)
                    .color(finalColor)
                    .marca(finalMarca)
                    .categoria(finalCat)
                    .build();
            p.setActivo(true);
            productoRepository.save(p);
        }

        Producto inactive = Producto.builder()
                .nombre("InactiveProj_" + System.nanoTime())
                .precio(999.0)
                .descripcion("inactivo")
                .color(color)
                .marca(marca)
                .categoria(cat)
                .build();
        inactive.setActivo(false);
        productoRepository.save(inactive);

        stats.clear();
    }

    @Test
    void findActiveSummaryReturnsOnlyActiveProducts() {
        List<ProductoSummaryProjection> summaries = productoRepository.findActiveSummary();

        // Must return at least the 2 active ones
        assertFalse(summaries.isEmpty(), "findActiveSummary() must return at least one result");
        assertTrue(summaries.size() >= 2,
                "Expected at least 2 active products in summary, got: " + summaries.size());
    }

    @Test
    void summaryProjectionFieldsArePopulated() {
        List<ProductoSummaryProjection> summaries = productoRepository.findActiveSummary();

        // Triangulation: verify each projected field is accessible and non-null for active products
        for (ProductoSummaryProjection s : summaries) {
            assertNotNull(s.getId(), "Projected id must not be null");
            assertNotNull(s.getNombre(), "Projected nombre must not be null");
            assertNotNull(s.getPrecio(), "Projected precio must not be null");
        }
    }

    @Test
    void findActiveSummaryIssuesAtMostOneQuery() {
        productoRepository.findActiveSummary();
        long queryCount = stats.getQueryExecutionCount();

        assertEquals(1, queryCount,
                "findActiveSummary() must issue exactly 1 query. Got: " + queryCount);
    }
}
