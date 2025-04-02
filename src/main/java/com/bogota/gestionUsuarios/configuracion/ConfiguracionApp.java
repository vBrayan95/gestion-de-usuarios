package com.bogota.gestionUsuarios.configuracion;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.bogota.gestionUsuarios.modelo.Usuario;
import com.bogota.gestionUsuarios.repositorio.UsuarioRepo;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class ConfiguracionApp {

	
	private final UsuarioRepo usuarioRepo;


	// de qué forma valida si el usuario existe o no
	 @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            final Usuario user = usuarioRepo.findByCorreo(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuaro no encontrado"));
            return org.springframework.security.core.userdetails.User
                    .builder()
                    .username(user.getCorreo())
                    .password(user.getClave())
                    .build();
        };
    }


	// forma de que el usuario y la contraseña sean los correctos
	 @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(codificadorClave());
        return authProvider;
    }


	 @Bean
    public AuthenticationManager authenticationManager(final AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }


	@Bean
	public PasswordEncoder codificadorClave() {
		return new BCryptPasswordEncoder();
	}

}
