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
}
