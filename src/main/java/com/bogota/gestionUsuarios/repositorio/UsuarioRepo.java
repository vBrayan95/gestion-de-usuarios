package com.bogota.gestionUsuarios.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bogota.gestionUsuarios.modelo.Usuario;

@Repository
public interface UsuarioRepo extends JpaRepository<Usuario, Long>{
	
	 boolean existsByCorreo(String correo);
}