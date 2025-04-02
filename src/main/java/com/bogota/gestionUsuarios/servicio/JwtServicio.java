package com.bogota.gestionUsuarios.servicio;

import java.util.Date;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.bogota.gestionUsuarios.modelo.Usuario;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtServicio {

    @Value("${application.security.jwt.secret-key}")
    private String llaveSecreta;
    @Value("${application.security.jwt.expiration}")
    private long vencimientoJwt;
    @Value("${application.security.jwt.refresh-token.expiration}")
    private long vencimientoRef;

    public String extraerNombreUsuario(String token) {
        return Jwts.parser()
                .verifyWith(obtenerLlaveFirmada())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public String generarToken(final Usuario usuario) {
        return construirToken(usuario, vencimientoJwt);
    }

    public String generarTokenRefresco(final Usuario usuario) {
        return construirToken(usuario, vencimientoRef);
    }

    private String construirToken(final Usuario usuario, final long vencimiento) {
        return Jwts
                .builder()
                .claims(Map.of("name", usuario.getNombre()))
                .subject(usuario.getCorreo()) // cómo se identifica al usuario en el token
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + vencimiento))
                .signWith(obtenerLlaveFirmada())
                .compact();
    }

    public boolean tokenValido(String token, Usuario usuario) {
        final String username = extraerNombreUsuario(token);
        return (username.equals(usuario.getCorreo())) && !tokenExpirado(token);
    }

    private boolean tokenExpirado(String token) {
        return extraerVencimiento(token).before(new Date());
    }

    private Date extraerVencimiento(String token) {
        return Jwts.parser()
                .verifyWith(obtenerLlaveFirmada())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration();
    }

    private SecretKey obtenerLlaveFirmada() {
        final byte[] keyBytes = Decoders.BASE64.decode(llaveSecreta);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
