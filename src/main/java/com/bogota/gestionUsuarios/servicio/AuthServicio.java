package com.bogota.gestionUsuarios.servicio;

import java.util.List;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bogota.gestionUsuarios.modelo.PeticionAuth;
import com.bogota.gestionUsuarios.modelo.PeticionRegistro;
import com.bogota.gestionUsuarios.modelo.RespuestaToken;
import com.bogota.gestionUsuarios.modelo.Token;
import com.bogota.gestionUsuarios.modelo.Usuario;
import com.bogota.gestionUsuarios.repositorio.TokenRepo;
import com.bogota.gestionUsuarios.repositorio.UsuarioRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServicio {

    private final PasswordEncoder passwordEncoder;
    private final UsuarioRepo usuarioRepo;
    private final JwtServicio jwtServicio;
    private final TokenRepo tokenRepo;
    private final AuthenticationManager authenticationManager;

	public RespuestaToken registrar(PeticionRegistro registro) {

        final Usuario usuario = Usuario.builder()
        .nombre(registro.nombre())
        .correo(registro.correo())
        .clave(passwordEncoder.encode(registro.clave()))
        .build();

        final Usuario usuarioGuardado = usuarioRepo.save(usuario);
        final String tokenJwt = jwtServicio.generarToken(usuarioGuardado);
        final String tokenRef = jwtServicio.generarTokenRefresco(usuarioGuardado);

        guardarTokenUsuario(usuarioGuardado, tokenJwt);
        return new RespuestaToken(tokenJwt, tokenRef);
	}

    public RespuestaToken ingresar (final PeticionAuth peticionAuth){

         authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        peticionAuth.email(),
                        peticionAuth.password()
                )
        );
        final Usuario usuario = usuarioRepo.findByCorreo(peticionAuth.email())
                .orElseThrow();
        final String tokenAcceso = jwtServicio.generarToken(usuario);
        final String refrescarToken = jwtServicio.generarTokenRefresco(usuario);
        revocarTokensUsuario(usuario);
        guardarTokenUsuario(usuario, tokenAcceso);
        return new RespuestaToken(tokenAcceso, refrescarToken);
    }


    private void guardarTokenUsuario(Usuario usuario, String tokenJwt) {
        final Token token = Token.builder()
        .token(tokenJwt)
        .tipoToken(Token.TokenType.BEARER)
        .revocado(false)
        .expirado(false)
        .usuario(usuario)
        .build();
        tokenRepo.save(token);
    }


    private void revocarTokensUsuario(final Usuario usuario) {
        final List<Token> tokensValidos = tokenRepo.findAllValidTokenByUsuario(usuario.getId());
        if (!tokensValidos.isEmpty()) {
            tokensValidos.forEach(token -> {
                token.setExpirado(true);
                token.setRevocado(true);
            });
            tokenRepo.saveAll(tokensValidos);
        }
    }

     public RespuestaToken refrescarToken(@NonNull final String authentication) {

        if (authentication == null || !authentication.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid auth header");
        }
        final String refrescarToken = authentication.substring(7);
        final String correoUsuario = jwtServicio.extraerNombreUsuario(refrescarToken);
        if (correoUsuario == null) {
            return null;
        }

        final Usuario usuario = this.usuarioRepo.findByCorreo(correoUsuario).orElseThrow();
        final boolean tokenValido = jwtServicio.tokenValido(refrescarToken, usuario);
        if (!tokenValido) {
            return null;
        }

        final String tokenAcceso = jwtServicio.generarTokenRefresco(usuario);
        revocarTokensUsuario(usuario);
        guardarTokenUsuario(usuario, tokenAcceso);

        return new RespuestaToken(tokenAcceso, refrescarToken);
    }

}
