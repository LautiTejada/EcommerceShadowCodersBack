package com.dresscode.api_dresscode.repositories;

import com.dresscode.api_dresscode.entities.Talle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TalleRepository extends JpaRepository<Talle,Long> {
}
