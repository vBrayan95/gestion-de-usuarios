package com.bogota.gestionUsuarios.servicio;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bogota.gestionUsuarios.modelo.Rol;
import com.bogota.gestionUsuarios.repositorio.RolRepo;


@Service
public class RolServicio {
    
    @Autowired
    private RolRepo rolRepo;

     public List<Rol> obtenerRoles() {
    	
    	return rolRepo.findAll();
    	
    }

	public Optional<Rol> obtenerRolPorId(Long id) {
		return rolRepo.findById(id);
	}

    public Rol agregarRol(Rol rol){

        return rolRepo.save(rol);
    }


    public Rol actualizarRolPorId(Long id, Rol nuevaInfoRol){
		
		Optional<Rol> antRol = rolRepo.findById(id);
		Rol rolObj = new Rol();
		
		if (antRol.isPresent()) {
			
			Rol nuevoRol = antRol.get();
			nuevoRol.setNombre(nuevaInfoRol.getNombre());
			nuevoRol.setObservacion(nuevaInfoRol.getObservacion());
			
			rolObj = rolRepo.save(nuevoRol);
		}
		
		return rolObj;
	}

    public void borrarRolPorId(Long id){

        rolRepo.deleteById(id);
    }
}
