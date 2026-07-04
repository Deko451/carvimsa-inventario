package com.carvimsa.inventario.repository;

import com.carvimsa.inventario.model.Usuario;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByUsername(String username);

    List<Usuario> findByRolNombre(String nombreRol);
}
