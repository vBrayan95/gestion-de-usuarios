package com.bogota.gestionUsuarios.servicio;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bogota.gestionUsuarios.modelo.Rol;
import com.bogota.gestionUsuarios.modelo.Usuario;
import com.bogota.gestionUsuarios.repositorio.RolRepo;
import com.bogota.gestionUsuarios.repositorio.UsuarioRepo;

@Service
public class UsuarioServicio {
	
	@Autowired
    private UsuarioRepo usuarioRepo;
	@Autowired
	private RolRepo rolRepo;
    
    public List<Usuario> obtenerUsuarios() {
    	
    	return usuarioRepo.findAll();
    	
    }

	public Optional<Usuario> obtenerUsuariosPorId(Long id) {
		// TODO Auto-generated method stub
		return usuarioRepo.findById(id);
	}

	public boolean correoRegistrado(String correo) {
		// TODO Auto-generated method stub
		return usuarioRepo.existsByCorreo(correo);
	}
	
	public Usuario agregarUsuario(Usuario usuario) {
		
		Rol rol = rolRepo.findById(usuario.getRol().getId()).orElseThrow(() -> new RuntimeException("Rol no encontrado"));
		
		usuario.setRol(rol);
	
		return usuarioRepo.save(usuario);
	}
	
	
	public Usuario actualizarUsuarioPorId(Long id, Usuario nuevaInfoUsuario){
		
		Optional<Usuario> antUsuario = usuarioRepo.findById(id);
		Usuario usuarioObj = new Usuario();
		
		if (antUsuario.isPresent()) {
			
			Usuario nuevoUsuario = antUsuario.get();
			nuevoUsuario.setNombre(nuevaInfoUsuario.getNombre());
			nuevoUsuario.setCorreo(nuevaInfoUsuario.getCorreo());
			nuevoUsuario.setRol(nuevaInfoUsuario.getRol());
			
			usuarioObj = usuarioRepo.save(nuevoUsuario);
		}
		
		return usuarioObj;
	}
	
	public void borrarUsuarioPorId(Long id) {
		
		usuarioRepo.deleteById(id);
	}
	
	
}
