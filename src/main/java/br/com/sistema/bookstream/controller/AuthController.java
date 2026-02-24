package br.com.sistema.bookstream.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.sistema.bookstream.dto.request.LoginRequest;
import br.com.sistema.bookstream.dto.request.UsuarioCadastroRequest;
import br.com.sistema.bookstream.dto.response.LoginResponse;
import br.com.sistema.bookstream.dto.response.UsuarioResponse;
import br.com.sistema.bookstream.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    // ==============================================================
    // login - Endpoint público. Recebe login e senha, valida contra
    // o banco e retorna um token JWT em caso de sucesso.
    // O token deve ser enviado no header Authorization: Bearer {token}
    // nas requisições subsequentes aos endpoints protegidos.
    // ==============================================================
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        log.info("===== LOGIN REQUEST RECEBIDO =====");
        log.info("Login: {}", request.login());
        log.info("Senha length: {}", request.senha() != null ? request.senha().length() : "null");
        log.info("Request object: {}", request);
        log.info("=====================================");
        
        try {
            log.info("Iniciando autenticação via AuthService...");
            LoginResponse response = authService.login(request);
            log.info("Login bem-sucedido para usuário: {}", request.login());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("❌ ERRO ao fazer login para usuário: {}", request.login());
            log.error("Exception: {}", e.getMessage());
            log.error("Cause: {}", e.getCause());
            log.error("Stack trace: ", e);
            throw e;
        }
    }

    // ==============================================================
    // register - Endpoint público para cadastro de novos usuários.
    // Recebe idCliente, login e senha, criptografa com BCrypt e
    // persiste no banco. Será restrito a admins após implementação
    // de controle de roles.
    // ==============================================================
    @PostMapping("/register")
    public ResponseEntity<UsuarioResponse> register(@RequestBody @Valid UsuarioCadastroRequest request) {
        log.info("===== REGISTER REQUEST RECEBIDO =====");
        log.info("ID Cliente: {}", request.idCliente());
        log.info("Login: {}", request.login());
        log.info("Senha length: {}", request.senha() != null ? request.senha().length() : "null");
        log.info("Request object: {}", request);
        log.info("=====================================");
        
        try {
            log.info("Iniciando cadastro via AuthService...");
            UsuarioResponse response = authService.register(request);
            log.info("Cadastro bem-sucedido para: {}", request.login());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("❌ ERRO ao registrar usuário: {}", request.login());
            log.error("Exception: {}", e.getMessage());
            log.error("Cause: {}", e.getCause());
            log.error("Stack trace: ", e);
            throw e;
        }
    }
}