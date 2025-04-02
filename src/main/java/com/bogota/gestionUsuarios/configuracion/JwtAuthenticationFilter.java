package com.bogota.gestionUsuarios.configuracion;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.bogota.gestionUsuarios.modelo.Usuario;
import com.bogota.gestionUsuarios.repositorio.TokenRepo;
import com.bogota.gestionUsuarios.repositorio.UsuarioRepo;
import com.bogota.gestionUsuarios.servicio.JwtServicio;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtServicio jwtServicio;
    private final UserDetailsService userDetailsService;
    private final TokenRepo tokenRepo;
    private final UsuarioRepo usuarioRepo;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        if (request.getServletPath().contains("/auth")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7);
        final String correoUsuario = jwtServicio.extraerNombreUsuario(jwt);
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (correoUsuario == null || authentication != null) {
            filterChain.doFilter(request, response);
            return;
        }

        final UserDetails userDetails = this.userDetailsService.loadUserByUsername(correoUsuario);
        final boolean isTokenExpiredOrRevoked = tokenRepo.findByToken(jwt)
                .map(token -> !token.getExpirado() && !token.getRevocado())
                .orElse(false);


        if (isTokenExpiredOrRevoked) {
            final Optional<Usuario> usuario = usuarioRepo.findByCorreo(correoUsuario);

            if (usuario.isPresent()) {
                final boolean tokenValido = jwtServicio.tokenValido(jwt, usuario.get());

                if (tokenValido) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}