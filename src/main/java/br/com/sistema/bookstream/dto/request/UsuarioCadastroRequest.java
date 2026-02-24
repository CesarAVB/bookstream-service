package br.com.sistema.bookstream.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UsuarioCadastroRequest(

        @NotNull(message = "O id do cliente é obrigatório")
        Long idCliente,

        @NotBlank(message = "O login é obrigatório")
        @Size(max = 100, message = "O login deve ter no máximo 100 caracteres")
        String login,

        @NotBlank(message = "A senha é obrigatória")
        @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres")
        String senha
) {}