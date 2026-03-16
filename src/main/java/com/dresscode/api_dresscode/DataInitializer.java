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
        OrdenDeCompraRepository ordenRepo
    ) {
        return args -> {
            // Crear usuario
            Usuario usuario = Usuario.builder()
                .username("testuser")
                .email("test@example.com")
                .password("1234")
                .rol(Usuario.Rol.USER)
                .build();
            usuarioRepo.save(usuario);

            // Crear dirección
            Direccion direccion = Direccion.builder()
                .calle("Calle Falsa")
                .numero(123)
                .codigoPostal(1000)
                .localidad("Ciudad")
                .provincia(Provincias.BUENOS_AIRES)
                .pais("Argentina")
                .build();
            direccionRepo.save(direccion);

            // Crear categoría
            Categoria categoria = Categoria.builder()
                .nombreCategoria("Remeras")
                .build();
            categoriaRepo.save(categoria);

            // Crear talle
            Talle talle = Talle.builder()
                .tipoTalle("M")
                .build();
            talleRepo.save(talle);

            // Crear producto
            Producto producto = Producto.builder()
                .nombre("Remera Test")
                .precio(500.0)
                .descripcion("Remera de prueba")
                .categoria(categoria)
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
