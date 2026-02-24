package br.com.sistema.bookstream.controller;

import br.com.sistema.bookstream.dto.request.UsuarioCadastroRequest;
import br.com.sistema.bookstream.dto.response.UsuarioResponse;
import br.com.sistema.bookstream.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    // ==============================================================
    // cadastrar - Endpoint restrito a admins autenticados. Recebe
    // idCliente, login e senha no body, criptografa a senha com
    // BCrypt e persiste o novo usuário no banco de dados.
    // ==============================================================
    @PostMapping
    public ResponseEntity<UsuarioResponse> cadastrar(@RequestBody @Valid UsuarioCadastroRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.cadastrar(request));
    }
}