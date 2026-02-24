package br.com.sistema.bookstream.controller;

import br.com.sistema.bookstream.dto.request.LoginRequest;
import br.com.sistema.bookstream.dto.request.UsuarioCadastroRequest;
import br.com.sistema.bookstream.dto.response.LoginResponse;
import br.com.sistema.bookstream.dto.response.UsuarioResponse;
import br.com.sistema.bookstream.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
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
        return ResponseEntity.ok(authService.login(request));
    }

    // ==============================================================
    // register - Endpoint público para cadastro de novos usuários.
    // Recebe idCliente, login e senha, criptografa com BCrypt e
    // persiste no banco. Será restrito a admins após implementação
    // de controle de roles.
    // ==============================================================
    @PostMapping("/register")
    public ResponseEntity<UsuarioResponse> register(@RequestBody @Valid UsuarioCadastroRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }
}