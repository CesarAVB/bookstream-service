package br.com.sistema.bookstream.config;

import br.com.sistema.bookstream.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    // ==============================================================
    // doFilterInternal - Intercepta cada requisição, extrai o token
    // do header Authorization, valida e autentica o usuário no
    // SecurityContext. Requisições sem token passam sem autenticação
    // e serão barradas pelo SecurityConfig caso o endpoint seja protegido.
    // ==============================================================
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        
    	String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            String login = jwtService.extrairLogin(token);

            if (login != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                if (jwtService.isTokenValido(token, login)) {
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(login, null, List.of());
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }
        } catch (Exception e) {
            log.warn("Erro ao processar token JWT: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}