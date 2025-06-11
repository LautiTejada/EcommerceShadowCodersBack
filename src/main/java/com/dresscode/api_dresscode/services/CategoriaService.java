package com.dresscode.api_dresscode.services;
import com.dresscode.api_dresscode.controllers.TipoController;
import com.dresscode.api_dresscode.dtos.CategoriaDTO;
import com.dresscode.api_dresscode.entities.Categoria;
import com.dresscode.api_dresscode.entities.Tipo;
import com.dresscode.api_dresscode.repositories.CategoriaRepository;
import com.dresscode.api_dresscode.repositories.TipoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoriaService extends BaseServiceImpl<Categoria, Long> {

    private final CategoriaRepository categoriaRepository;
    private final TipoRepository tipoRepository;

    @Override
    protected JpaRepository<Categoria, Long> getRepository() {
        return categoriaRepository;
    }

    public Categoria createCategoria(CategoriaDTO categoria, Long tipoId) {
        Tipo tipo = tipoRepository.findById(tipoId)
                .orElseThrow(()-> new RuntimeException("Tipo no encontrado con el id: "+ tipoId));

        Categoria categoriaNueva =  Categoria.builder()
                .nombreCategoria(categoria.getNombreCategoria())
                .tipo(tipo)
                .build();
                return categoriaRepository.save(categoriaNueva);
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

    @Override
    @Transactional
    public Categoria changeStatus(Long id) {
        Categoria categoria = findById(id);

        boolean nuevoEstado = !categoria.getActivo();
        categoria.setActivo(nuevoEstado);

        if (!nuevoEstado) {
            categoria.getProductos().forEach(producto -> producto.setActivo(false));
        }

        return categoriaRepository.save(categoria);
    }

    @Override
    @Transactional
    public Categoria desactivate(Long id) {
        Categoria categoria = findById(id);
        if (!categoria.getActivo()) {
            throw new RuntimeException("La categoria ya está desactivada.");
        }

        categoria.setActivo(false);
        categoria.getProductos().forEach(producto -> producto.setActivo(false));
        return categoriaRepository.save(categoria);
    }
}