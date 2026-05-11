package com.dresscode.api_dresscode.repositories;

import com.dresscode.api_dresscode.entities.Base;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.List;

@NoRepositoryBean
public interface BaseRepository<E extends Base, ID extends Serializable> extends JpaRepository<E, ID> {

    /**
     * Returns only active entities by issuing a DB-level WHERE clause.
     * Replaces the previous in-memory {@code stream().filter(Base::getActivo)} pattern.
     *
     * The {@code #{#entityName}} placeholder is resolved per-repository by Spring Data JPA.
     */
    @Query("SELECT e FROM #{#entityName} e WHERE e.activo = true")
    List<E> findAllActive();
}
