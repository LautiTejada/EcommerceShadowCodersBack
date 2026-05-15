package com.dresscode.api_dresscode.query;

import com.dresscode.api_dresscode.entities.*;
import com.dresscode.api_dresscode.repositories.FavoritoRepository;
import com.dresscode.api_dresscode.repositories.MarcaRepository;
import com.dresscode.api_dresscode.repositories.ColorRepository;
import com.dresscode.api_dresscode.repositories.CategoriaRepository;
import com.dresscode.api_dresscode.repositories.TipoRepository;
import com.dresscode.api_dresscode.repositories.ProductoRepository;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Task 5.2 — Verifies that fetching Favoritos with their associated Productos
 * does NOT cause N+1 queries. Uses Hibernate statistics to count SQL statements.
 *
 * Spec: "total number of SQL SELECT statements MUST NOT exceed a constant bound (<=3)
 * regardless of result size".
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class N1QueryEliminationTest {

    @Autowired
    private FavoritoRepository favoritoRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
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
    private Usuario usuario;
    private static final int FAVORITO_COUNT = 5;

    @BeforeEach
    void setUp() {
        stats = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();

        // Seed supporting entities
        Marca marca = Marca.builder().nombreMarca("MarcaTest_N1_" + System.nanoTime()).build();
        marca.setActivo(true);
        marca = marcaRepository.save(marca);

        Color color = Color.builder().nombreColor("ColorTest_N1_" + System.nanoTime()).build();
        color.setActivo(true);
        color = colorRepository.save(color);

        Tipo tipo = new Tipo();
        tipo.setNombre("TipoTest_N1_" + System.nanoTime());
        tipo.setActivo(true);
        tipo = tipoRepository.save(tipo);

        Categoria cat = Categoria.builder()
                .nombreCategoria("CatTest_N1_" + System.nanoTime())
                .tipo(tipo)
                .build();
        cat.setActivo(true);
        cat = categoriaRepository.save(cat);

        usuario = Usuario.builder()
                .username("n1user_" + System.nanoTime())
                .email("n1_" + System.nanoTime() + "@test.com")
                .password("{bcrypt}$2a$10$test")
                .rol(Usuario.Rol.USER)
                .build();
        usuario.setActivo(true);
        usuario = usuarioRepository.save(usuario);

        // Create FAVORITO_COUNT favoritos, each pointing to a different producto
        for (int i = 0; i < FAVORITO_COUNT; i++) {
            Producto p = Producto.builder()
                    .nombre("Producto_N1_" + i + "_" + System.nanoTime())
                    .precio(100.0 + i)
                    .descripcion("desc")
                    .color(color)
                    .marca(marca)
                    .categoria(cat)
                    .build();
            p.setActivo(true);
            p = productoRepository.save(p);

            Favorito f = Favorito.builder()
                    .usuario(usuario)
                    .producto(p)
                    .fechaAgregado(LocalDateTime.now())
                    .build();
            f.setActivo(true);
            favoritoRepository.save(f);
        }

        stats.clear(); // reset AFTER seeding — only measure the list query
    }

    @Test
    void fetchFavoritosWithProductoUsesAtMostThreeQueries() {
        // This calls the JOIN FETCH query that loads Favorito + Producto in one go
        List<Favorito> favoritos = favoritoRepository.findActivosByUsuarioWithProducto(usuario);

        long queryCount = stats.getQueryExecutionCount();

        // With N+1: 1 (list) + FAVORITO_COUNT (one per product access) = 6 queries for 5 items
        // With JOIN FETCH: exactly 1 query
        // Spec allows <= 3 as a generous bound
        assertTrue(queryCount <= 3,
                "Loading " + FAVORITO_COUNT + " favoritos should use <= 3 queries. " +
                "Actual: " + queryCount + ". N+1 pattern detected.");

        assertEquals(FAVORITO_COUNT, favoritos.size(),
                "All " + FAVORITO_COUNT + " active favoritos should be returned");
    }

    @Test
    void eachFavoritoHasProductoLoadedWithoutAdditionalQuery() {
        List<Favorito> favoritos = favoritoRepository.findActivosByUsuarioWithProducto(usuario);
        long queriesAfterFetch = stats.getQueryExecutionCount();

        // Accessing producto on each favorito MUST NOT trigger additional queries
        // because JOIN FETCH already loaded them
        for (Favorito f : favoritos) {
            assertNotNull(f.getProducto(), "Producto must be loaded (not null) for each Favorito");
            assertNotNull(f.getProducto().getNombre(), "Producto.nombre must be accessible without additional query");
        }

        // Query count must remain the same — no new queries fired during attribute access
        assertEquals(queriesAfterFetch, stats.getQueryExecutionCount(),
                "Accessing Producto on loaded Favoritos should NOT trigger additional queries. " +
                "JOIN FETCH must have pre-loaded all Productos.");
    }
}
