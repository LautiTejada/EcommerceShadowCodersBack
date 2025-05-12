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

    // Traer todos los tipos
    public List<Tipo> getAllTipos() {
        return tipoRepository.findAll();
    }

    // Traer un tipo por su ID
    public Tipo getTipoById(Long id) {
        return tipoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tipo no encontrado con id: " + id));
    }

    // Crear un nuevo tipo
    public Tipo createTipo(Tipo tipo) {
        return tipoRepository.save(tipo);
    }

    // Actualizar un tipo existente
    @Transactional
    public Tipo updateTipo(Long id, Tipo tipoActualizado) {
        Tipo tipoExistente = getTipoById(id);
        tipoExistente.setNombre(tipoActualizado.getNombre());
        return tipoRepository.save(tipoExistente);
    }

    // Eliminar un tipo, validando que no tenga categorías asociadas
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
