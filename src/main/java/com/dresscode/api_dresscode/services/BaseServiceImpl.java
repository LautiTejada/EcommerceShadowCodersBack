package com.dresscode.api_dresscode.services;

import com.dresscode.api_dresscode.entities.Base;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

@RequiredArgsConstructor
@Transactional
public abstract class BaseServiceImpl<E extends Base, ID extends Serializable> implements BaseService<E, ID> {

    protected abstract JpaRepository<E, ID> getRepository();

    @Override
    public List<E> findAll() {
        return getRepository().findAll();
    }

    @Override
    public Page<E> findAll(Pageable pageable) {
        return getRepository().findAll(pageable);
    }

    @Override
    public E findById(ID id) {
        return getRepository().findById(id)
                .orElseThrow(() -> new RuntimeException("Entidad no encontrada con id: " + id));
    }

    @Override
    public E save(E entity) {
        return getRepository().save(entity);
    }

    @Override
    public E update(ID id, E entity) {
        if (!getRepository().existsById(id)) {
            throw new RuntimeException("Entidad no encontrada con id: " + id);
        }
        return getRepository().save(entity);
    }

    @Override
    public boolean delete(ID id) {
        if (!getRepository().existsById(id)) {
            throw new RuntimeException("Entidad no encontrada con id: " + id);
        }
        getRepository().deleteById(id);
        return true;
    }
}

