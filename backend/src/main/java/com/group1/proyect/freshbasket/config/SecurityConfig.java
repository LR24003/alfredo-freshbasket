package com.group1.proyect.freshbasket.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));
        http.csrf(csrf -> csrf.disable());
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.authorizeHttpRequests(auth -> auth

                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // RUTAS PÚBLICAS (Swagger, Auth y visualización libre de países)
                .requestMatchers(
                        "/v3/api-docs/**", "/v3/api-docs.yaml",
                        "/swagger-ui/**", "/swagger-ui.html", "/webjars/**",
                        "/api/auth/**", "/api/countries/**"
                ).permitAll()


                .requestMatchers(HttpMethod.GET, "/api/cart/**").hasAnyAuthority("ADMINISTRADOR", "EMPLEADO", "CLIENTE", "SOPORTE")
                .requestMatchers(HttpMethod.POST, "/api/cart/**").hasAnyAuthority("ADMINISTRADOR", "EMPLEADO", "CLIENTE", "SOPORTE")
                .requestMatchers(HttpMethod.PUT, "/api/cart/**").hasAnyAuthority("ADMINISTRADOR", "EMPLEADO", "CLIENTE", "SOPORTE")
                .requestMatchers(HttpMethod.DELETE, "/api/cart/**").hasAnyAuthority("ADMINISTRADOR", "EMPLEADO", "CLIENTE", "SOPORTE")


                .requestMatchers("/api/categories/**", "/api/suppliers/**")
                .hasAnyAuthority("ADMINISTRADOR", "EMPLEADO", "CLIENTE", "SOPORTE")

                // GET lo ven todos. POST/PUT/DELETE quedan restringidos a personal interno
                .requestMatchers(HttpMethod.GET, "/api/products/**").hasAnyAuthority("ADMINISTRADOR", "EMPLEADO", "CLIENTE", "SOPORTE")
                .requestMatchers(HttpMethod.POST, "/api/products/**").hasAnyAuthority("ADMINISTRADOR", "EMPLEADO", "SOPORTE")
                .requestMatchers(HttpMethod.PUT, "/api/products/**").hasAnyAuthority("ADMINISTRADOR", "SOPORTE")
                .requestMatchers(HttpMethod.DELETE, "/api/products/**").hasAuthority("ADMINISTRADOR")
                .requestMatchers(HttpMethod.GET, "/api/products/alerts/low-stock").hasAnyRole("ADMINISTRADOR")


                // Empleados y soporte ven y crean (GET y POST). Admin y Soporte editan (PUT). Admin borra.
                .requestMatchers(HttpMethod.GET, "/api/entries/**", "/api/exits/**").hasAnyAuthority("ADMINISTRADOR", "EMPLEADO", "SOPORTE")
                .requestMatchers(HttpMethod.POST, "/api/entries/**", "/api/exits/**").hasAnyAuthority("ADMINISTRADOR", "EMPLEADO", "SOPORTE")
                .requestMatchers(HttpMethod.PUT, "/api/entries/**", "/api/exits/**").hasAnyAuthority("ADMINISTRADOR", "SOPORTE")
                .requestMatchers(HttpMethod.DELETE, "/api/entries/**", "/api/exits/**").hasAuthority("ADMINISTRADOR")

                // PERMISOS DEL PERFIL PROPIO (Usuarios autenticados)
                .requestMatchers("/api/users/me").authenticated()

                // GET lo ve Soporte/Admin. El resto de acciones (POST, PUT, DELETE) solo Admin
                .requestMatchers(HttpMethod.GET, "/api/users/**").hasAnyAuthority("ADMINISTRADOR", "SOPORTE", "EMPLEADO")
                .requestMatchers(HttpMethod.POST, "/api/users/**").hasAnyAuthority("ADMINISTRADOR", "SOPORTE", "EMPLEADO")
                .requestMatchers("/api/users/**").hasAuthority("ADMINISTRADOR")

                .requestMatchers(HttpMethod.GET, "/api/sales/my-purchases").hasAnyAuthority("ADMINISTRADOR", "EMPLEADO", "CLIENTE", "SOPORTE")
                .requestMatchers(HttpMethod.GET, "/api/sales/status/**", "/api/sales", "/api/sales/{id}").hasAnyAuthority("ADMINISTRADOR", "EMPLEADO", "SOPORTE")
                .requestMatchers(HttpMethod.GET, "/api/sales/{saleId}/details").hasAnyRole("EMPLEADO", "ADMINISTRADOR")
                .requestMatchers(HttpMethod.POST, "/api/sales/**").hasAnyAuthority("ADMINISTRADOR", "EMPLEADO")
                .requestMatchers(HttpMethod.PUT, "/api/sales/**").hasAuthority("ADMINISTRADOR")
                .requestMatchers(HttpMethod.DELETE, "/api/sales/**").hasAuthority("ADMINISTRADOR")

                // Seguridad global
                .anyRequest().authenticated()
        );

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
    @Bean
    public org.springframework.web.cors.CorsConfigurationSource corsConfigurationSource() {
        org.springframework.web.cors.CorsConfiguration configuration = new org.springframework.web.cors.CorsConfiguration();

        configuration.setAllowedOrigins(List.of(
                "http://localhost:5173",
                "http://127.0.0.1:5173",
                "http://192.168.1.60:5173",
                "http://localhost",
                "http://127.0.0.1",
                "http://192.168.1.60",
                "http://localhost:81"
        ));

        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "Cache-Control", "X-Requested-With",
                "Accept", "Origin", "Access-Control-Request-Method", "Access-Control-Request-Headers"));
        configuration.setAllowCredentials(true);

        org.springframework.web.cors.UrlBasedCorsConfigurationSource source = new org.springframework.web.cors.UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}