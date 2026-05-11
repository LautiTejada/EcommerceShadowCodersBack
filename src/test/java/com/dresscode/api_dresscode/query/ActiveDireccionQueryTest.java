package com.dresscode.api_dresscode.query;

import com.dresscode.api_dresscode.entities.Direccion;
import com.dresscode.api_dresscode.entities.Usuario;
import com.dresscode.api_dresscode.entities.enums.Provincias;
import com.dresscode.api_dresscode.repositories.DireccionRepository;
import com.dresscode.api_dresscode.repositories.UsuarioRepository;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Task 5.1 (targeted) — Verifies that finding the first active Direccion for a user
 * uses a DB-level WHERE clause rather than loading all direcciones in-memory then filtering.
 *
 * Spec: "All filtering logic MUST be expressed as database-level predicates."
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ActiveDireccionQueryTest {

    @Autowired
    private DireccionRepository direccionRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    private Statistics stats;
    private Usuario usuario;
    private Long activaDireccionId;

    @BeforeEach
    void setUp() {
        stats = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();

        usuario = Usuario.builder()
                .username("dirtest_" + System.nanoTime())
                .email("dir_" + System.nanoTime() + "@test.com")
                .password("{bcrypt}$2a$10$test")
                .rol(Usuario.Rol.USER)
                .build();
        usuario.setActivo(true);
        usuario = usuarioRepository.save(usuario);

        // Seed: 1 inactive + 1 active address
        Direccion inactiva = Direccion.builder()
                .calle("Calle Inactiva")
                .numero(1)
                .codigoPostal(1000)
                .localidad("Buenos Aires")
                .provincia(Provincias.BUENOS_AIRES)
                .pais("Argentina")
                .usuario(usuario)
                .build();
        inactiva.setActivo(false);
        direccionRepository.save(inactiva);

        Direccion activa = Direccion.builder()
                .calle("Avenida Activa")
                .numero(100)
                .codigoPostal(1001)
                .localidad("Rosario")
                .provincia(Provincias.SANTA_FE)
                .pais("Argentina")
                .usuario(usuario)
                .build();
        activa.setActivo(true);
        Direccion saved = direccionRepository.save(activa);
        activaDireccionId = saved.getId();

        stats.clear();
    }

    @Test
    void findFirstActiveByUsuarioIdReturnsActiveDireccion() {
        Optional<Direccion> result = direccionRepository.findFirstByUsuarioIdAndActivoTrue(usuario.getId());

        assertTrue(result.isPresent(), "Must find the active direccion for the user");
        assertEquals(activaDireccionId, result.get().getId(),
                "Must return the active direccion, not the inactive one");
        assertTrue(result.get().getActivo(),
                "Returned direccion must have activo=true");
    }

    @Test
    void findFirstActiveByUsuarioIdIssuesAtMostOneQuery() {
        direccionRepository.findFirstByUsuarioIdAndActivoTrue(usuario.getId());
        long queryCount = stats.getQueryExecutionCount();

        // Must be exactly 1 DB query with WHERE usuario_id=? AND activo=true
        assertEquals(1, queryCount,
                "findFirstByUsuarioIdAndActivoTrue() must issue exactly 1 SQL query. " +
                "Got: " + queryCount + ". In-memory filter would require loading all rows first.");
    }

    @Test
    void findFirstActiveByUsuarioIdReturnsEmptyWhenNoActiveAddress() {
        // Create a user with only inactive addresses
        Usuario userNoActive = Usuario.builder()
                .username("noactive_" + System.nanoTime())
                .email("noactive_" + System.nanoTime() + "@test.com")
                .password("{bcrypt}$2a$10$test")
                .rol(Usuario.Rol.USER)
                .build();
        userNoActive.setActivo(true);
        userNoActive = usuarioRepository.save(userNoActive);

        Direccion inactiva = Direccion.builder()
                .calle("Calle Sin Activa")
                .numero(999)
                .codigoPostal(1000)
                .localidad("La Plata")
                .provincia(Provincias.BUENOS_AIRES)
                .pais("Argentina")
                .usuario(userNoActive)
                .build();
        inactiva.setActivo(false);
        direccionRepository.save(inactiva);

        stats.clear();
        Optional<Direccion> result = direccionRepository.findFirstByUsuarioIdAndActivoTrue(userNoActive.getId());

        assertFalse(result.isPresent(),
                "Must return empty Optional when the user has no active addresses");
    }
}
