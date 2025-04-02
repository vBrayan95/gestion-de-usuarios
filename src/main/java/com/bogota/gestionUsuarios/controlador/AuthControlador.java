package com.bogota.gestionUsuarios.controlador;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpHeaders;
import com.bogota.gestionUsuarios.modelo.PeticionAuth;
import com.bogota.gestionUsuarios.modelo.PeticionRegistro;
import com.bogota.gestionUsuarios.modelo.RespuestaToken;
import com.bogota.gestionUsuarios.servicio.AuthServicio;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthControlador {
	
    @Autowired
	private final AuthServicio authServicio;
	
	@PostMapping("/registrar")
	public ResponseEntity<RespuestaToken> registrar(@RequestBody final PeticionRegistro registro){
		final RespuestaToken respuesta = authServicio.registrar(registro);
        return ResponseEntity.ok(respuesta);
	}
	
	
	@PostMapping("/ingresar")
	public ResponseEntity<RespuestaToken> ingresar(@RequestBody final PeticionAuth ingreso){
        final RespuestaToken respuesta = authServicio.ingresar(ingreso);
        return ResponseEntity.ok(respuesta);
	}
	
	
	@PostMapping("/refrescar-token")
	public RespuestaToken refrescar(@RequestHeader(HttpHeaders.AUTHORIZATION) final String encabezadoAuth){
		final RespuestaToken respuesta = authServicio.refrescarToken(encabezadoAuth);
        return respuesta;
	}
}
