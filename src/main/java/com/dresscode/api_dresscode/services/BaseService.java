package com.dresscode.api_dresscode.services;

import com.dresscode.api_dresscode.entities.Base;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.Serializable;
import java.util.List;

public interface BaseService<E extends Base, ID extends Serializable> {

    List<E> findAll();

    Page<E> findAll(Pageable pageable);

    E findById(ID id);

    E save(E entity);

    E update(ID id, E entity);

    boolean delete(ID id);
}