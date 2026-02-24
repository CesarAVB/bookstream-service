package br.com.sistema.bookstream.service;

import br.com.sistema.bookstream.dto.request.UsuarioCadastroRequest;
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
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    // ==============================================================
    // cadastrar - Recebe os dados do novo usuário, valida se o login
    // já está em uso, criptografa a senha com BCrypt e persiste no
    // banco. A senha original nunca é armazenada.
    // ==============================================================
    @Transactional
    public UsuarioResponse cadastrar(UsuarioCadastroRequest request) {
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