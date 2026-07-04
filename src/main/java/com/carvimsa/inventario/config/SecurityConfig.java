package com.carvimsa.inventario.config;

import com.carvimsa.inventario.service.SeguridadService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuración de Spring Security: rutas protegidas por rol (Paso 5, Regla 3
 * de INSTRUCCIONES_CLAUDE_CODE.md), formulario de login personalizado y logout.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final SeguridadService seguridadService;

    public SecurityConfig(SeguridadService seguridadService) {
        this.seguridadService = seguridadService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(passwordEncoder());
        provider.setUserDetailsService(seguridadService);
        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/css/**", "/js/**", "/img/**").permitAll()
                        .requestMatchers("/dashboard").hasAnyRole("Operario", "Jefe", "Administrador")
                        .requestMatchers("/ingresos/**").hasAnyRole("Operario", "Jefe", "Administrador")
                        .requestMatchers("/salidas/**").hasAnyRole("Operario", "Jefe", "Administrador")
                        .requestMatchers("/stock/**").hasAnyRole("Operario", "Jefe", "Administrador")
                        .requestMatchers("/pedidos/**").hasAnyRole("Operario", "Jefe", "Administrador")
                        .requestMatchers("/alertas/**").hasAnyRole("Jefe", "Administrador")
                        .requestMatchers("/reportes/**").hasAnyRole("Jefe", "Administrador")
                        .requestMatchers("/admin/**").hasRole("Administrador")
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/dashboard", true)
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll())
                .authenticationProvider(authenticationProvider());

        return http.build();
    }
}
