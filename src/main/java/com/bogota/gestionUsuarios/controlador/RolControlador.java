package com.bogota.gestionUsuarios.controlador;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.bogota.gestionUsuarios.modelo.Rol;
import com.bogota.gestionUsuarios.repositorio.RolRepo;

@RestController
public class RolControlador {
	
	@Autowired
	private RolRepo rolRepo;
	
	@GetMapping("/obtenerRoles")
	public ResponseEntity<List<Rol>> obtenerRoles() {
		
		try {
			
			List<Rol> listaRoles = new ArrayList<Rol>();
			rolRepo.findAll().forEach(listaRoles::add);
			
			if (listaRoles.isEmpty()) {
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}
			
			return new ResponseEntity<List<Rol>>(listaRoles,HttpStatus.OK);
			
		} catch (Exception exception) {
			// TODO: handle exception
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/obtenerRolesPorId/{id}")
	public ResponseEntity<Rol> obtenerRolesPorId(@PathVariable Long id) {
		
		Optional<Rol> infoRol = rolRepo.findById(id);
		
		
		if (infoRol.isPresent()) {
			
			return new ResponseEntity<>(infoRol.get(), HttpStatus.OK);
		}
		
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}
	
	@PostMapping("/agregarRol")
	public ResponseEntity<Rol> agregarRol(@RequestBody Rol rol) {
		
		
		Rol rolObj = rolRepo.save(rol);
		
		return new ResponseEntity<>(rolObj, HttpStatus.OK);
		
	}
	
	@PostMapping("actualizarRolPorId/{id}")
	public ResponseEntity<Rol> actualizarRolPorId(@PathVariable Long id, @RequestBody Rol nuevaInfoRol) {
		
		
		Optional<Rol> antRol = rolRepo.findById(id);
		
		if (antRol.isPresent()) {
			
			Rol nuevoRol = antRol.get();
			nuevoRol.setNombre(nuevaInfoRol.getNombre());
			nuevoRol.setObservacion(nuevaInfoRol.getObservacion());
			
			Rol RolObj = rolRepo.save(nuevoRol);
			return new ResponseEntity<>(RolObj, HttpStatus.OK);
		}
		
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);

	}
	
	@DeleteMapping("borrarRolPorId/{id}")
	public ResponseEntity<HttpStatus> borrarRolPorId(@PathVariable Long id) {
		
		rolRepo.deleteById(id);
		return new ResponseEntity<>(HttpStatus.OK);

	}

}
