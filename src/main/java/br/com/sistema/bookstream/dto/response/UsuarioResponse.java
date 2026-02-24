package br.com.sistema.bookstream.dto.response;

import br.com.sistema.bookstream.entity.Usuario;

import java.time.LocalDateTime;

public record UsuarioResponse(
        Long id,
        Long idCliente,
        String login,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static UsuarioResponse from(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getIdCliente(),
                usuario.getLogin(),
                usuario.getCreatedAt(),
                usuario.getUpdatedAt()
        );
    }
}