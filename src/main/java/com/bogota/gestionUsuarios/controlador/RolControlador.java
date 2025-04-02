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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bogota.gestionUsuarios.modelo.Rol;
import com.bogota.gestionUsuarios.servicio.RolServicio;

@RestController
@RequestMapping("/api")
public class RolControlador {
	
	@Autowired
	private RolServicio rolServicio;
	
	@GetMapping("/obtenerRoles")
	public ResponseEntity<List<Rol>> obtenerRoles() {
		
		try {
			
			List<Rol> listaRoles = rolServicio.obtenerRoles();
			
			if (listaRoles.isEmpty()) {
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}
			
			return new ResponseEntity<List<Rol>>(listaRoles,HttpStatus.OK);
			
		} catch (Exception exception) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/obtenerRolesPorId/{id}")
	public ResponseEntity<Rol> obtenerRolesPorId(@PathVariable Long id) {
		
		Optional<Rol> infoRol = rolServicio.obtenerRolPorId(id);
		
		
		if (infoRol.isPresent()) {
			
			return new ResponseEntity<>(infoRol.get(), HttpStatus.OK);
		}
		
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}
	
	@PostMapping("/agregarRol")
	public ResponseEntity<Rol> agregarRol(@RequestBody Rol rol) {
		
		
		Rol rolObj = rolServicio.agregarRol(rol);
		
		return new ResponseEntity<>(rolObj, HttpStatus.OK);
		
	}
	
	@PostMapping("actualizarRolPorId/{id}")
	public ResponseEntity<Rol> actualizarRolPorId(@PathVariable Long id, @RequestBody Rol nuevaInfoRol) {
		
		
		Rol rolResp = rolServicio.actualizarRolPorId(id, nuevaInfoRol);
		
		return new ResponseEntity<>(rolResp, rolResp.getId() != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
		
	}
	
	@DeleteMapping("borrarRolPorId/{id}")
	public ResponseEntity<HttpStatus> borrarRolPorId(@PathVariable Long id) {
		
		rolServicio.borrarRolPorId(id);
		return new ResponseEntity<>(HttpStatus.OK);

	}

}
