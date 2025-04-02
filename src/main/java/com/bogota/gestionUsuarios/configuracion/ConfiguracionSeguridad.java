package com.bogota.gestionUsuarios.configuracion;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.bogota.gestionUsuarios.modelo.Token;
import com.bogota.gestionUsuarios.repositorio.TokenRepo;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpHeaders;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class ConfiguracionSeguridad {
    
    @Value("${spring.security.user.name}")
	private String usuario;
	@Value("${spring.security.user.password}")
	private String clave;
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final TokenRepo tokenRepo;
    
    @Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		http
        .csrf(csrf -> csrf.disable()) // Deshabilitar CSRF para evitar conflictos con H2 Console
        .authorizeHttpRequests(authorize -> authorize
        .requestMatchers("/h2-console/**").permitAll()
        .requestMatchers("/auth/**").permitAll()
        .anyRequest()
        .authenticated())
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
        .logout(logout ->
                        logout.logoutUrl("/auth/logout")
                                .addLogoutHandler(this::logout)
                                .logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext()))
        .httpBasic(Customizer.withDefaults())
        .headers(headers -> headers
        .frameOptions(frameOptions -> frameOptions.sameOrigin()) // Permitir frames solo desde el mismo origen
		);

		return http.build();
	}

    private void logout(
            final HttpServletRequest request, final HttpServletResponse response,
            final Authentication authentication
    ) {

        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }

        final String jwt = authHeader.substring(7);
        final Token tokenAlmacenado = tokenRepo.findByToken(jwt)
                .orElse(null);
        if (tokenAlmacenado != null) {
            tokenAlmacenado.setExpirado(true);
            tokenAlmacenado.setRevocado(true);
            tokenRepo.save(tokenAlmacenado);
            SecurityContextHolder.clearContext();
        }
    }

    @Bean
	public InMemoryUserDetailsManager user() {

		return new InMemoryUserDetailsManager(
				User.withUsername(usuario)
						.password(clave)
						.build());
	}
}