package com.dresscode.api_dresscode.services;

import com.dresscode.api_dresscode.entities.Categoria;
import com.dresscode.api_dresscode.entities.Producto;
import com.dresscode.api_dresscode.entities.Tipo;
import com.dresscode.api_dresscode.repositories.CategoriaRepository;
import com.dresscode.api_dresscode.repositories.TipoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TipoService {

    private final TipoRepository tipoRepository;

    private final CategoriaRepository categoriaRepository;


    public List<Tipo> getAllTipos() {
        return tipoRepository.findAll();
    }


    public Tipo getTipoById(Long id) {
        return tipoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tipo no encontrado con id: " + id));
    }


    public Tipo createTipo(Tipo tipo) {
        return tipoRepository.save(tipo);
    }


    @Transactional
    public Tipo updateTipo(Long id, Tipo tipoActualizado) {
        Tipo tipoExistente = getTipoById(id);
        tipoExistente.setNombre(tipoActualizado.getNombre());
        return tipoRepository.save(tipoExistente);
    }


    @Transactional
    public void deleteTipo(Long id) {
        Tipo tipoExistente = getTipoById(id);
        if (!tipoExistente.getCategorias().isEmpty()) {
            throw new RuntimeException("No se puede eliminar el tipo porque tiene categorías asociadas.");
        }
        tipoRepository.delete(tipoExistente);
    }

    public List<Categoria> getCategoriasByTipoId(Long tipoId) {
        Tipo tipo = getTipoById(tipoId);
        return categoriaRepository.findByTipoId(tipoId);
    }
}
