package br.com.sistema.bookstream.config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.Arrays;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Value("${app.cors.allowed-origins:http://localhost:5173,http://127.0.0.1:5173}")
    private String allowedOrigins;

    // ✅ INJETAR JwtAuthFilter
    private final JwtAuthFilter jwtAuthFilter;

    // ==============================================================
    // corsConfigurationSource - Configura CORS para o Spring Security.
    // Sem isto, o Spring Security bloqueia requisições CORS antes
    // de chegar ao resto da aplicação.
    // ==============================================================
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Converter String para Array
        String[] origins = allowedOrigins.split(",");
        configuration.setAllowedOrigins(Arrays.asList(origins));
        
        log.info("🔐 CORS configurado para origens: {}", allowedOrigins);
        for (String origin : origins) {
            log.info("✅ Origem permitida: {}", origin.trim());
        }
        
        // Métodos permitidos
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        
        // Headers permitidos
        configuration.setAllowedHeaders(Arrays.asList("*"));
        
        // Headers expostos
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Disposition", "X-Total-Count"));
        
        // Permitir credenciais (cookies, auth)
        configuration.setAllowCredentials(true);
        
        // Cache de 1 hora
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        log.info("✅ CORS inicializado com sucesso!");
        
        return source;
    }

    // ==============================================================
    // securityFilterChain - Define as regras de segurança da aplicação.
    // Endpoints de login são públicos. Todos os demais exigem
    // autenticação. Sessão stateless pois usaremos JWT.
    // IMPORTANTE: .cors() DEVE vir ANTES de .csrf() e outras configs
    // IMPORTANTE: .addFilterBefore() DEVE vir ANTES de .build()
    // ==============================================================
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // ✅ CORS PRIMEIRO (antes de tudo)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // Desabilitar CSRF
                .csrf(AbstractHttpConfigurer::disable)
                // Sessão stateless
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Autorização
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .anyRequest().authenticated()
                )
                // JwtAuthFilter ANTES do UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    // ==============================================================
    // passwordEncoder - Bean do BCryptPasswordEncoder usado para
    // criptografar e comparar senhas. Injetado no UsuarioService
    // e futuramente no AuthService para validação no login.
    // ==============================================================
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}