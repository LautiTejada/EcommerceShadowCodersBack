package com.dresscode.api_dresscode.query;

import com.dresscode.api_dresscode.entities.Talle;
import com.dresscode.api_dresscode.repositories.TalleRepository;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Task 5.1 — Verifies that findAllActive() issues a WHERE clause at the DB level
 * rather than loading all records and filtering in-memory.
 *
 * Uses {@link Talle} (simple entity, no FK dependencies) seeded via
 * {@link TalleRepository} which extends BaseRepository.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class FindAllActiveQueryTest {

    @Autowired
    private TalleRepository talleRepository;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    private Statistics stats;

    @BeforeEach
    void setUp() {
        talleRepository.deleteAll();

        stats = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
        stats.clear();

        // Seed: 2 active + 1 inactive
        Talle activo1 = new Talle();
        activo1.setTipoTalle("S");
        activo1.setActivo(true);
        talleRepository.save(activo1);

        Talle activo2 = new Talle();
        activo2.setTipoTalle("M");
        activo2.setActivo(true);
        talleRepository.save(activo2);

        Talle inactivo = new Talle();
        inactivo.setTipoTalle("DESCONTINUADO");
        inactivo.setActivo(false);
        talleRepository.save(inactivo);

        stats.clear(); // reset after seeding — only measure the findAllActive query
    }

    @Test
    void findAllActiveReturnsOnlyActiveRecords() {
        List<Talle> activos = talleRepository.findAllActive();

        // Must return exactly the 2 active ones, not the inactive one
        assertEquals(2, activos.size(),
                "findAllActive() must return only active records (2 seeded), got: " + activos.size());
        assertTrue(activos.stream().allMatch(t -> Boolean.TRUE.equals(t.getActivo())),
                "findAllActive() must return ONLY records with activo=true");
    }

    @Test
    void findAllActiveExcludesInactiveRecords() {
        List<Talle> activos = talleRepository.findAllActive();

        // Triangulation: prove the inactive record was excluded by DB filtering
        assertFalse(activos.stream().anyMatch(t -> "DESCONTINUADO".equals(t.getTipoTalle())),
                "findAllActive() must NOT return inactive records (activo=false)");
    }

    @Test
    void findAllActiveIssuesAtMostOneQuery() {
        talleRepository.findAllActive();
        long queryCount = stats.getQueryExecutionCount();

        // Must be exactly 1 JPQL query with WHERE activo = true
        assertEquals(1, queryCount,
                "findAllActive() must issue exactly 1 SQL query with a WHERE clause. " +
                "Current count: " + queryCount);
    }
}
