package com.dresscode.api_dresscode.services;

import com.dresscode.api_dresscode.dtos.TipoDTO;
import com.dresscode.api_dresscode.entities.Categoria;
import com.dresscode.api_dresscode.entities.Producto;
import com.dresscode.api_dresscode.entities.Tipo;
import com.dresscode.api_dresscode.repositories.CategoriaRepository;
import com.dresscode.api_dresscode.repositories.TipoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TipoService extends BaseServiceImpl<Tipo, Long>{

    private final TipoRepository tipoRepository;
    private final CategoriaRepository categoriaRepository;

    @Override
    protected JpaRepository<Tipo, Long> getRepository(){return tipoRepository;}



    @Transactional
    public void deleteTipo(Long id) {
        Tipo tipoExistente = findById(id);
        if (!tipoExistente.getCategorias().isEmpty()) {
            throw new RuntimeException("No se puede eliminar el tipo porque tiene categorías asociadas.");
        }
        tipoRepository.delete(tipoExistente);
    }

    public List<Categoria> getCategoriasByTipoId(Long tipoId) {
        return categoriaRepository.findByTipoId(tipoId);
    }

    @Override
    @Transactional
    public Tipo changeStatus(Long id) {
        Tipo tipo = findById(id);

        boolean nuevoEstado = !tipo.getActivo();
        tipo.setActivo(nuevoEstado);


        if (!nuevoEstado) {
            tipo.getCategorias().forEach(categoria -> {
                categoria.setActivo(false);
                categoria.getProductos().forEach(producto -> producto.setActivo(false));
            });
        }

        return tipoRepository.save(tipo);
    }

    @Override
    @Transactional
    public Tipo desactivate(Long id) {
        Tipo tipo = findById(id);
        if (!tipo.getActivo()) {
            throw new RuntimeException("El tipo ya está desactivado.");
        }

        tipo.setActivo(false);
        tipo.getCategorias().forEach(categoria -> categoria.setActivo(false));
        return tipoRepository.save(tipo);
    }
}
