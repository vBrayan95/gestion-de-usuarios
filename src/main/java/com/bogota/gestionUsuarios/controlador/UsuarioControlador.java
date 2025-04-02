package com.bogota.gestionUsuarios.controlador;

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
import com.bogota.gestionUsuarios.modelo.Usuario;
import com.bogota.gestionUsuarios.servicio.UsuarioServicio;

@RestController
@RequestMapping("/api")
public class UsuarioControlador {

	
    @Autowired
    private UsuarioServicio usuarioServicio;
    
	@GetMapping("/obtenerUsuarios")
	public ResponseEntity<List<Usuario>> obtenerUsuarios() {
		
		try {
			
			List<Usuario> listaUsuarios = usuarioServicio.obtenerUsuarios();
			
			if (listaUsuarios.isEmpty()) {
	    		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	    	}
			
			return new ResponseEntity<List<Usuario>>(listaUsuarios,HttpStatus.OK);
			
		} catch (Exception e) {
			// TODO: handle exception
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}

	@GetMapping("/obtenerUsuariosPorId/{id}")
	public ResponseEntity<Usuario> obtenerUsuariosPorId(@PathVariable Long id) {
		
		Optional<Usuario> infoUsuario = usuarioServicio.obtenerUsuariosPorId(id);
		
		if (infoUsuario.isPresent()) {
			
			return new ResponseEntity<>(infoUsuario.get(), HttpStatus.OK);
		}
		
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

	
	@PostMapping("/agregarUsuario")
	public ResponseEntity<Usuario> agregarUsuario(@RequestBody Usuario usuario) {
		
		
//		 validación de correo existente
		if (usuarioServicio.correoRegistrado(usuario.getCorreo())) {
			return new ResponseEntity<>(new Usuario(), HttpStatus.CONFLICT);
		}
		
		
		Usuario usuarioObj = usuarioServicio.agregarUsuario(usuario);
		
		return new ResponseEntity<>(usuarioObj, HttpStatus.OK);
		
	}

	@PostMapping("actualizarUsuarioPorId/{id}")
	public ResponseEntity<Usuario> actualizarUsuarioPorId(@PathVariable Long id, @RequestBody Usuario nuevaInfoUsuario) {
		
		
		Usuario usuarioResp = usuarioServicio.actualizarUsuarioPorId(id, nuevaInfoUsuario);
		return new ResponseEntity<>( usuarioResp, usuarioResp.getId() != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);

	}

	@DeleteMapping("borrarUsuarioPorId/{id}")
	public ResponseEntity<HttpStatus> borrarUsuarioPorId(@PathVariable Long id) {
		
		usuarioServicio.borrarUsuarioPorId(id);
		return new ResponseEntity<>(HttpStatus.OK);

	}
}
