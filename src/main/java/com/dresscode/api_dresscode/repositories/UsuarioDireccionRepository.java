package com.dresscode.api_dresscode.repositories;

import com.dresscode.api_dresscode.entities.Direccion;
import com.dresscode.api_dresscode.entities.UsuarioDireccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsuarioDireccionRepository extends JpaRepository<UsuarioDireccion, Long> {
    @Query("SELECT ud.direccion FROM UsuarioDireccion ud WHERE ud.usuario.id = :usuarioId")
    List<Direccion> findDireccionesByUsuarioId(@Param("usuarioId") Long usuarioId);
}
