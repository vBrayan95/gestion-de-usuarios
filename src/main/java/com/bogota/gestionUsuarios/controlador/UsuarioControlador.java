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
import com.bogota.gestionUsuarios.modelo.Usuario;
import com.bogota.gestionUsuarios.repositorio.RolRepo;
import com.bogota.gestionUsuarios.repositorio.UsuarioRepo;

@RestController
public class UsuarioControlador {

	
    @Autowired
    private UsuarioRepo usuarioRepo;
    @Autowired
    private RolRepo rolRepo;

	
	@GetMapping("/obtenerUsuarios")
	public ResponseEntity<List<Usuario>> obtenerUsuarios() {
		
		try {
			
			List<Usuario> listaUsuarios = new ArrayList<Usuario>();
			usuarioRepo.findAll().forEach(listaUsuarios::add);
			
			if (listaUsuarios.isEmpty()) {
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}
			
			return new ResponseEntity<List<Usuario>>(listaUsuarios,HttpStatus.OK);
			
		} catch (Exception exception) {
			// TODO: handle exception
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/obtenerUsuariosPorId/{id}")
	public ResponseEntity<Usuario> obtenerUsuariosPorId(@PathVariable Long id) {
		
		Optional<Usuario> infoUsuario = usuarioRepo.findById(id);
		
		
		if (infoUsuario.isPresent()) {
			
			return new ResponseEntity<>(infoUsuario.get(), HttpStatus.OK);
		}
		
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

	
	@PostMapping("/agregarUsuario")
	public ResponseEntity<Usuario> agregarUsuario(@RequestBody Usuario usuario) {
		
		
//		 validación de correo existente
		if (usuarioRepo.existsByCorreo(usuario.getCorreo())) {
			return new ResponseEntity<>(new Usuario(), HttpStatus.CONFLICT);
		}
		
//		try {
//		    usuarioRepo.save(usuario);
//		} catch (DataIntegrityViolationException e) {
//		    throw new RuntimeException("El correo ya existe.");
//		}
		
		Rol rol = rolRepo.findById(usuario.getRol().getId()).orElseThrow(() -> new RuntimeException("Rol no encontrado"));
		
		usuario.setRol(rol);
	
		Usuario usuarioObj = usuarioRepo.save(usuario);
		
		return new ResponseEntity<>(usuarioObj, HttpStatus.OK);
		
	}

	@PostMapping("actualizarUsuarioPorId/{id}")
	public ResponseEntity<Usuario> actualizarUsuarioPorId(@PathVariable Long id, @RequestBody Usuario nuevaInfoUsuario) {
		
		
		Optional<Usuario> antUsuario = usuarioRepo.findById(id);
		
		if (antUsuario.isPresent()) {
			
			Usuario nuevoUsuario = antUsuario.get();
			nuevoUsuario.setNombre(nuevaInfoUsuario.getNombre());
			nuevoUsuario.setCorreo(nuevaInfoUsuario.getCorreo());
			nuevoUsuario.setRol(nuevaInfoUsuario.getRol());
			
			Usuario usuarioObj = usuarioRepo.save(nuevoUsuario);
			return new ResponseEntity<>(usuarioObj, HttpStatus.OK);
		}
		
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);

	}

	@DeleteMapping("borrarUsuarioPorId/{id}")
	public ResponseEntity<HttpStatus> borrarUsuarioPorId(@PathVariable Long id) {
		
		usuarioRepo.deleteById(id);
		return new ResponseEntity<>(HttpStatus.OK);

	}
}
