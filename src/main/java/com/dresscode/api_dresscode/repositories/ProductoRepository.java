package com.dresscode.api_dresscode.repositories;

import com.dresscode.api_dresscode.entities.Categoria;
import com.dresscode.api_dresscode.entities.Producto;
import com.dresscode.api_dresscode.entities.enums.Marca;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends BaseRepository<Producto, Long> {
    List<Producto> findByCategoria(Categoria categoria);
    @Query("SELECT p FROM Producto p WHERE " +
            "(:categoriaIds IS NULL OR p.categoria.id IN :categoriaIds) AND " +
            "(:marcas IS NULL OR p.marca IN :marcas) AND " +
            "(:precioMin IS NULL OR p.precio >= :precioMin) AND " +
            "(:precioMax IS NULL OR p.precio <= :precioMax)")
    List<Producto> filtrarProductos(
            @Param("categoriaIds") List<Long> categoriaIds,
            @Param("marcas") List<Marca> marcas,
            @Param("precioMin") Integer precioMin,
            @Param("precioMax") Integer precioMax
    );
}
