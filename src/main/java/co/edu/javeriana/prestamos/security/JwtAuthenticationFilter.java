package co.edu.javeriana.prestamos.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        String method = request.getMethod();
        
        System.out.println("🔍 JwtFilter - Ruta: " + path + " | Método: " + method);
        
        // ⭐ EXCLUIR COMPLETAMENTE EL FILTRO PARA ENDPOINTS PÚBLICOS
        boolean shouldSkip = path.startsWith("/api/auth/") ||
               path.startsWith("/swagger-ui") ||
               path.startsWith("/v3/api-docs") ||
               path.startsWith("/swagger-resources") ||
               path.startsWith("/webjars") ||
               path.startsWith("/h2-console");
        
        System.out.println("🔍 JwtFilter - Saltar filtro: " + shouldSkip);
        return shouldSkip;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        
        System.out.println("🔍 JwtFilter - Procesando petición: " + request.getRequestURI());
        
        final String authHeader = request.getHeader("Authorization");
        
        // Solo procesar JWT si existe el header
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("🔍 JwtFilter - No hay token JWT, continuando...");
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7);
        System.out.println("🔍 JwtFilter - Token JWT encontrado");

        try {
            final String username = jwtService.extractUsername(jwt);
            System.out.println("🔍 JwtFilter - Usuario extraído: " + username);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                System.out.println("🔍 JwtFilter - UserDetails cargado: " + userDetails.getUsername());
                
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    System.out.println("🔍 JwtFilter - Token válido, estableciendo autenticación");
                    
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    
                    System.out.println("🔍 JwtFilter - Autenticación establecida para: " + username);
                } else {
                    System.out.println("🔍 JwtFilter - Token inválido");
                }
            }

        } catch (Exception e) {
            System.err.println("❌ ERROR en JwtFilter: " + e.getMessage());
            e.printStackTrace();
        }

        filterChain.doFilter(request, response);
    }
}