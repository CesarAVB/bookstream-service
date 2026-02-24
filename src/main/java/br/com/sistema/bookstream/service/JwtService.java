package br.com.sistema.bookstream.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Slf4j
@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiracao-ms}")
    private Long expiracaoMs;

    // ==============================================================
    // gerarToken - Gera um token JWT assinado com o segredo configurado
    // no application.properties. O subject é o login do usuário e
    // o claim "idCliente" é incluído para uso nos endpoints protegidos.
    // ==============================================================
    public String gerarToken(String login, Long idCliente) {
        return Jwts.builder()
                .setSubject(login)
                .claim("idCliente", idCliente)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiracaoMs))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // ==============================================================
    // extrairLogin - Extrai o login (subject) do token JWT.
    // Utilizado no filtro de autenticação para identificar o usuário.
    // ==============================================================
    public String extrairLogin(String token) {
        return extrairClaims(token).getSubject();
    }

    // ==============================================================
    // extrairIdCliente - Extrai o claim idCliente do token JWT.
    // Pode ser usado nos controllers para identificar o cliente logado.
    // ==============================================================
    public Long extrairIdCliente(String token) {
        return extrairClaims(token).get("idCliente", Long.class);
    }

    // ==============================================================
    // isTokenValido - Verifica se o token é válido para o login
    // informado e se não está expirado.
    // ==============================================================
    public boolean isTokenValido(String token, String login) {
        try {
            return extrairLogin(token).equals(login) && !isTokenExpirado(token);
        } catch (Exception e) {
            log.warn("Token inválido: {}", e.getMessage());
            return false;
        }
    }

    // ==============================================================
    // getExpiracaoMs - Retorna o tempo de expiração configurado em ms.
    // Usado no LoginResponse para informar o frontend quando o token expira.
    // ==============================================================
    public Long getExpiracaoMs() {
        return expiracaoMs;
    }

    // ==============================================================
    // isTokenExpirado - Verifica se a data de expiração do token
    // já passou em relação ao momento atual.
    // ==============================================================
    private boolean isTokenExpirado(String token) {
        return extrairClaims(token).getExpiration().before(new Date());
    }

    // ==============================================================
    // extrairClaims - Faz o parse e valida a assinatura do token,
    // retornando o objeto Claims com todos os dados contidos nele.
    // ==============================================================
    private Claims extrairClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // ==============================================================
    // getKey - Converte o segredo configurado em uma chave HMAC
    // compatível com o algoritmo HS256 usado na assinatura do token.
    // ==============================================================
    private Key getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
}