package com.dresscode.api_dresscode.repositories;

import com.dresscode.api_dresscode.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario,Long> {
}
