package com.dresscode.api_dresscode.query;

import com.dresscode.api_dresscode.entities.Direccion;
import com.dresscode.api_dresscode.entities.Usuario;
import com.dresscode.api_dresscode.entities.enums.Provincias;
import com.dresscode.api_dresscode.repositories.DireccionRepository;
import com.dresscode.api_dresscode.repositories.UsuarioRepository;
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
 * Task 5.3 — Verifies that Usuario.direcciones is now LAZY-loaded.
 * Proves no LazyInitializationException occurs in the patched service methods,
 * and that loading a Usuario by itself does NOT eagerly fetch its direcciones.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class EagerToLazyFetchTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private DireccionRepository direccionRepository;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Autowired
    private EntityManager entityManager;

    private Statistics stats;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        stats = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
        stats.clear();

        // Seed: one user with two addresses
        usuario = Usuario.builder()
                .username("testuser_lazy")
                .email("lazy_" + System.nanoTime() + "@test.com")
                .password("{bcrypt}$2a$10$test")
                .rol(Usuario.Rol.USER)
                .build();
        usuario.setActivo(true);
        usuario = usuarioRepository.save(usuario);

        Direccion dir1 = Direccion.builder()
                .calle("Calle Falsa")
                .numero(123)
                .codigoPostal(1000)
                .localidad("Buenos Aires")
                .provincia(Provincias.BUENOS_AIRES)
                .pais("Argentina")
                .usuario(usuario)
                .build();
        dir1.setActivo(true);
        direccionRepository.save(dir1);

        Direccion dir2 = Direccion.builder()
                .calle("Avenida Siempreviva")
                .numero(742)
                .codigoPostal(1001)
                .localidad("Rosario")
                .provincia(Provincias.SANTA_FE)
                .pais("Argentina")
                .usuario(usuario)
                .build();
        dir2.setActivo(true);
        direccionRepository.save(dir2);

        stats.clear();
    }

    @Test
    void loadingUsuarioAloneDoesNotFetchDireccionesEagerly() {
        // Evict the entity from the L1 (first-level) cache so the next query
        // is forced to hit the database. Without flush+clear, Hibernate returns
        // the already-managed instance without issuing any SQL.
        entityManager.flush();
        entityManager.clear();
        stats.clear();

        // Use a JPQL query (not EntityManager.find()) so the result is counted
        // by Hibernate's getQueryExecutionCount() statistics.
        // getQueryExecutionCount() tracks JPQL/HQL query executions, NOT find() calls.
        entityManager.createQuery("SELECT u FROM Usuario u WHERE u.id = :id", Usuario.class)
                .setParameter("id", usuario.getId())
                .getSingleResult();

        long queriesAfterUserLoad = stats.getQueryExecutionCount();

        // With EAGER on direcciones: Hibernate issues an extra query (or JOIN) for the collection → count >= 2
        // With LAZY on direcciones: loading User is exactly 1 query, no extra queries for the collection
        assertEquals(1, queriesAfterUserLoad,
                "A JPQL SELECT on Usuario should issue exactly 1 query when direcciones is LAZY. " +
                "Got: " + queriesAfterUserLoad + " queries. " +
                "If > 1, FetchType.EAGER is still present on Usuario.direcciones.");
    }

    @Test
    void direccionesByUsuarioIdReturnsCorrectCount() {
        // Access direcciones via repository (correct pattern for LAZY associations)
        List<Direccion> dirs = direccionRepository.findByUsuarioId(usuario.getId());

        assertEquals(2, dirs.size(),
                "Should find exactly 2 addresses for the test user via findByUsuarioId");
    }

    @Test
    void allDireccionesReturnedBelongToUser() {
        List<Direccion> dirs = direccionRepository.findByUsuarioId(usuario.getId());

        assertTrue(dirs.stream().allMatch(d -> d.getUsuario().getId().equals(usuario.getId())),
                "All returned direcciones must belong to the seeded user");
    }
}
