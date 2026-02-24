package br.com.sistema.bookstream.repository;

import br.com.sistema.bookstream.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Busca usuário pelo login — usado no cadastro e na autenticação
    Optional<Usuario> findByLogin(String login);

    // Verifica se já existe um usuário com o login informado
    boolean existsByLogin(String login);
}