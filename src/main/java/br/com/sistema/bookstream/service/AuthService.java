package br.com.sistema.bookstream.service;

import br.com.sistema.bookstream.dto.request.LoginRequest;
import br.com.sistema.bookstream.dto.request.UsuarioCadastroRequest;
import br.com.sistema.bookstream.dto.response.LoginResponse;
import br.com.sistema.bookstream.dto.response.UsuarioResponse;
import br.com.sistema.bookstream.entity.Usuario;
import br.com.sistema.bookstream.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    // ==============================================================
    // login - Valida o login e senha do usuário contra o banco de dados.
    // Compara a senha informada com o hash BCrypt armazenado.
    // Retorna um token JWT com o login e idCliente em caso de sucesso.
    // Lança IllegalArgumentException com mensagem genérica para não
    // revelar se é o login ou a senha que está errado.
    // ==============================================================
    public LoginResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByLogin(request.login()).orElseThrow(() -> new IllegalArgumentException("Login ou senha inválidos."));

        if (!passwordEncoder.matches(request.senha(), usuario.getSenha())) {
            throw new IllegalArgumentException("Login ou senha inválidos.");
        }

        String token = jwtService.gerarToken(usuario.getLogin(), usuario.getIdCliente());
        log.info("Login efetuado: login={} idCliente={}", usuario.getLogin(), usuario.getIdCliente());

        return LoginResponse.of(token, usuario.getIdCliente(), usuario.getLogin(), jwtService.getExpiracaoMs());
    }

    // ==============================================================
    // register - Valida se o login já está em uso, criptografa a
    // senha com BCrypt e persiste o novo usuário no banco de dados.
    // A senha original nunca é armazenada.
    // ==============================================================
    @Transactional
    public UsuarioResponse register(UsuarioCadastroRequest request) {
        if (usuarioRepository.existsByLogin(request.login())) {
            throw new IllegalArgumentException("Já existe um usuário com o login informado.");
        }

        Usuario usuario = Usuario.builder()
                .idCliente(request.idCliente())
                .login(request.login())
                .senha(passwordEncoder.encode(request.senha()))
                .build();

        Usuario salvo = usuarioRepository.save(usuario);
        log.info("Usuário cadastrado: login={} idCliente={}", salvo.getLogin(), salvo.getIdCliente());

        return UsuarioResponse.from(salvo);
    }
}