package com.dresscode.api_dresscode;

import com.dresscode.api_dresscode.entities.*;
import com.dresscode.api_dresscode.repositories.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {
    @Bean
    CommandLineRunner initData(
        UsuarioRepository usuarioRepo,
        TipoRepository tipoRepo,
        CategoriaRepository categoriaRepo,
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
        };
    }
}
