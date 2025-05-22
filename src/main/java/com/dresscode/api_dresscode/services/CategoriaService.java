package com.dresscode.api_dresscode.services;

import com.dresscode.api_dresscode.dtos.CategoriaDTO;
import com.dresscode.api_dresscode.entities.Categoria;
import com.dresscode.api_dresscode.entities.Producto;
import com.dresscode.api_dresscode.entities.Tipo;
import com.dresscode.api_dresscode.repositories.CategoriaRepository;
import com.dresscode.api_dresscode.repositories.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriaService extends BaseServiceImpl<Categoria, Long> {

    private final CategoriaRepository categoriaRepository;
    private final TipoService tipoService;

    @Override
    protected JpaRepository<Categoria, Long> getRepository() {
        return categoriaRepository;
    }



    @Override
    @Transactional
    public Categoria update(Long id, Categoria categoriaActualizada) {
        Categoria categoriaExistente = findById(id);
        categoriaExistente.setNombreCategoria(categoriaActualizada.getNombreCategoria());
        categoriaExistente.setTipo(categoriaActualizada.getTipo());
        return categoriaRepository.save(categoriaExistente);
    }

    @Override
    @Transactional
    public boolean delete(Long id) {
        Categoria categoria = findById(id);
        if (!categoria.getProductos().isEmpty()) {
            throw new RuntimeException("No se puede eliminar la categoría porque tiene productos asociados.");
        }
        categoriaRepository.delete(categoria);
        return true;
    }
}