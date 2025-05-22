package com.dresscode.api_dresscode.repositories;

import com.dresscode.api_dresscode.entities.Tipo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoRepository extends BaseRepository<Tipo, Long> {

}
