package com.dresscode.api_dresscode.query;

import com.dresscode.api_dresscode.entities.*;
import com.dresscode.api_dresscode.repositories.*;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Task 6.1 — Parameterized Hibernate statistics integration tests for converted list endpoints.
 *
 * For each list endpoint, seeds N rows, calls the repository, and asserts:
 * queryExecutionCount <= constant (independent of N).
 *
 * Spec: NFR-3 — "List endpoint SQL count <= constant; validated by Hibernate statistics in tests."
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class HibernateStatisticsListEndpointTest {

    @Autowired private TalleRepository talleRepository;
    @Autowired private MarcaRepository marcaRepository;
    @Autowired private ProductoRepository productoRepository;
    @Autowired private ColorRepository colorRepository;
    @Autowired private CategoriaRepository categoriaRepository;
    @Autowired private TipoRepository tipoRepository;
    @Autowired private EntityManagerFactory entityManagerFactory;

    private Statistics stats;

    @BeforeEach
    void setUp() {
        stats = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
    }

    // --- findAllActive() on Talle (simplest entity, no FKs) ---

    @ParameterizedTest
    @ValueSource(ints = {3, 10, 25})
    void findAllActiveTalle_queryCountBoundedRegardlessOfN(int n) {
        talleRepository.deleteAll();
        for (int i = 0; i < n; i++) {
            Talle t = new Talle();
            t.setTipoTalle("T_" + i + "_" + System.nanoTime());
            t.setActivo(i % 3 != 0); // mix of active/inactive
            talleRepository.save(t);
        }
        stats.clear();

        List<Talle> result = talleRepository.findAllActive();
        long queryCount = stats.getQueryExecutionCount();

        // findAllActive() must always cost exactly 1 query, regardless of N
        assertEquals(1, queryCount,
                "findAllActive() for " + n + " rows must issue 1 query; got " + queryCount);
        // All returned are active
        assertTrue(result.stream().allMatch(t -> Boolean.TRUE.equals(t.getActivo())),
                "findAllActive() must return only active records");
    }

    // --- findActiveSummary() on Producto (projection) ---

    @Test
    void findActiveSummaryProducto_queryCountIsOne() {
        Marca marca = Marca.builder().nombreMarca("MarcaStat_" + System.nanoTime()).build();
        marca.setActivo(true);
        marca = marcaRepository.save(marca);

        Color color = Color.builder().nombreColor("ColorStat_" + System.nanoTime()).build();
        color.setActivo(true);
        color = colorRepository.save(color);

        Tipo tipo = new Tipo();
        tipo.setNombre("TipoStat_" + System.nanoTime());
        tipo.setActivo(true);
        tipo = tipoRepository.save(tipo);

        Categoria cat = Categoria.builder()
                .nombreCategoria("CatStat_" + System.nanoTime())
                .tipo(tipo)
                .build();
        cat.setActivo(true);
        cat = categoriaRepository.save(cat);

        Marca finalMarca = marca;
        Color finalColor = color;
        Categoria finalCat = cat;

        // Seed 5 active productos
        for (int i = 0; i < 5; i++) {
            Producto p = Producto.builder()
                    .nombre("ProdStat_" + i + "_" + System.nanoTime())
                    .precio(200.0 + i)
                    .descripcion("d")
                    .color(finalColor)
                    .marca(finalMarca)
                    .categoria(finalCat)
                    .build();
            p.setActivo(true);
            productoRepository.save(p);
        }

        stats.clear();
        productoRepository.findActiveSummary();
        long queryCount = stats.getQueryExecutionCount();

        assertEquals(1, queryCount,
                "findActiveSummary() must issue exactly 1 query for the projection. Got: " + queryCount);
    }

    // --- findActivosByUsuarioWithProducto() — covered in N1QueryEliminationTest ---
    // See N1QueryEliminationTest for the bounded query count assertion on Favorito list.
}
