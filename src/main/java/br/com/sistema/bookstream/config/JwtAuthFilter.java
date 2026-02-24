package br.com.sistema.bookstream.config;
import java.io.IOException;
import java.util.ArrayList;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import br.com.sistema.bookstream.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        String method = request.getMethod();
        
        log.debug("🔍 [JwtAuthFilter] {} {}", method, path);

        String authHeader = request.getHeader("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.debug("⚠️ Nenhum token Bearer encontrado");
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        log.debug("📝 Token encontrado: {}", token.substring(0, Math.min(50, token.length())) + "...");

        try {
            // 1. Extrair login do token
            String login = jwtService.extrairLogin(token);
            log.debug("👤 Login extraído: {}", login);

            // 2. Verificar se já não tem autenticação
            if (login != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                log.debug("🔐 Validando token para: {}", login);

                // 3. Validar token
                if (jwtService.isTokenValido(token, login)) {
                    log.info("✅ Token válido! Usuário: {}", login);

                    // 4. Criar autenticação com authorities vazia (pode adicionar roles depois)
                    UsernamePasswordAuthenticationToken auth = 
                            new UsernamePasswordAuthenticationToken(
                                    login, 
                                    null, 
                                    new ArrayList<>() // Authorities vazia - pode customizar depois
                            );
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    
                    // 5. Setar no contexto de segurança
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    log.info("🔐 Autenticação setada no contexto para: {}", login);
                } else {
                    log.warn("❌ Token inválido para usuário: {}", login);
                }
            } else if (login == null) {
                log.warn("❌ Não foi possível extrair login do token");
            } else {
                log.debug("⚠️ Já existe autenticação no contexto");
            }
        } catch (Exception e) {
            log.error("❌ Erro ao processar token JWT: {}", e.getMessage());
            log.debug("Stack trace:", e);
        }

        log.debug("➡️ Continuando filtro");
        filterChain.doFilter(request, response);
    }
}