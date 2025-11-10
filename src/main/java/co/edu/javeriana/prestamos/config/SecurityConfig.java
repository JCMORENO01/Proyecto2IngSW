package co.edu.javeriana.prestamos.config;

import co.edu.javeriana.prestamos.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;

    // ⭐ VERSIÓN TEMPORAL: PERMITIR TODO para diagnosticar
    @Bean
    @Order(1)
    public SecurityFilterChain permitAllFilterChain(HttpSecurity http) throws Exception {
        System.out.println("🔒 INICIANDO CONFIGURACIÓN DE SEGURIDAD - PERMITIENDO TODO");
        
        return http
            .securityMatcher("/**")
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> {
                System.out.println("🔒 DESHABILITANDO CSRF");
                csrf.disable();
            })
            .authorizeHttpRequests(auth -> {
                System.out.println("🔒 CONFIGURANDO AUTORIZACIÓN: PERMITIENDO TODAS LAS PETICIONES");
                auth.anyRequest().permitAll(); // ⭐ PERMITIR TODO TEMPORALMENTE
            })
            .sessionManagement(session -> {
                System.out.println("🔒 CONFIGURANDO SESIÓN STATELESS");
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
            })
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        System.out.println("🔒 CONFIGURANDO CORS PARA TODOS LOS ORÍGENES");
        
        configuration.setAllowedOriginPatterns(Arrays.asList("*")); // Permitir todos los orígenes
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setExposedHeaders(Arrays.asList("Authorization"));
        configuration.setAllowCredentials(false); // Debe ser false cuando se usa "*"
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}