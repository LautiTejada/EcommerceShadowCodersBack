package com.dresscode.api_dresscode;

import com.dresscode.api_dresscode.entities.*;
import com.dresscode.api_dresscode.repositories.*;
import com.dresscode.api_dresscode.entities.enums.Provincias;
import com.dresscode.api_dresscode.entities.enums.EstadoOrden;
import com.dresscode.api_dresscode.entities.enums.MetodoPago;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {
    @Bean
    CommandLineRunner initData(
        UsuarioRepository usuarioRepo,
        DireccionRepository direccionRepo,
        ProductoRepository productoRepo,
        TalleRepository talleRepo,
        CategoriaRepository categoriaRepo,
        OrdenDeCompraRepository ordenRepo,
        TipoRepository tipoRepo,
        ColorRepository colorRepo,
        MarcaRepository marcaRepo
    ) {
        return args -> {
            // Crear usuario solo si no existe el email
            Usuario usuario = null;
            if (!usuarioRepo.existsByEmail("test@example.com")) {
                usuario = Usuario.builder()
                    .username("testuser")
                    .email("test@example.com")
                    .password("1234")
                    .rol(Usuario.Rol.USER)
                    .build();
                usuarioRepo.save(usuario);
            } else {
                usuario = usuarioRepo.findByEmail("test@example.com").orElse(null);
            }

            // Crear dirección
            Direccion direccion = Direccion.builder()
                .calle("Calle Falsa")
                .numero(123)
                .codigoPostal(1000)
                .localidad("Ciudad")
                .provincia(Provincias.BUENOS_AIRES)
                .pais("Argentina")
                .usuario(usuario)
                .build();
            direccionRepo.save(direccion);

            // Crear tipo
            Tipo tipo = tipoRepo.findByNombre("Indumentaria").orElse(null);
            if (tipo == null) {
                tipo = Tipo.builder()
                    .nombre("Indumentaria")
                    .build();
                tipoRepo.save(tipo);
            }

            // Crear categoría
            Categoria categoria = categoriaRepo.findByNombreCategoria("Remeras").orElse(null);
            if (categoria == null) {
                categoria = Categoria.builder()
                    .nombreCategoria("Remeras")
                    .tipo(tipo)
                    .build();
                categoriaRepo.save(categoria);
            }

            // Crear talle
            Talle talle = Talle.builder()
                .tipoTalle("M")
                .build();
            talleRepo.save(talle);

            // Crear color
            Color color = colorRepo.findByNombreColor("Negro").orElse(null);
            if (color == null) {
                color = Color.builder()
                    .nombreColor("Negro")
                    .build();
                colorRepo.save(color);
            }

            // Crear marca
            Marca marca = marcaRepo.findByNombreMarca("Genérica").orElse(null);
            if (marca == null) {
                marca = Marca.builder()
                    .nombreMarca("Genérica")
                    .build();
                marcaRepo.save(marca);
            }

            // Crear producto
            Producto producto = Producto.builder()
                .nombre("Remera Test")
                .precio(500.0)
                .descripcion("Remera de prueba")
                .categoria(categoria)
                .color(color)
                .marca(marca)
                .build();
            productoRepo.save(producto);

            // Crear orden de compra
            OrdenDeCompra orden = OrdenDeCompra.builder()
                .usuario(usuario)
                .direccion(direccion)
                .fecha(java.time.LocalDate.now())
                .precioTotal(500.0)
                .metodoPago(MetodoPago.MERCADO_PAGO)
                .estadoOrden(EstadoOrden.PEDIDO)
                .build();
            ordenRepo.save(orden);
        };
    }
}
