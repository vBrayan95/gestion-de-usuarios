package com.bogota.gestionUsuarios.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bogota.gestionUsuarios.modelo.Rol;

@Repository
public interface RolRepo  extends JpaRepository<Rol, Long>{

}
