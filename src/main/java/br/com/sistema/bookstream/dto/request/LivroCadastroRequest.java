package br.com.sistema.bookstream.dto.request;

import br.com.sistema.bookstream.entity.enums.TipoLivro;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record LivroCadastroRequest(

        @NotBlank(message = "O nome é obrigatório")
        @Size(max = 255, message = "O nome deve ter no máximo 255 caracteres")
        String nome,

        @Size(max = 255, message = "O autor deve ter no máximo 255 caracteres")
        String autor,

        String descricao,

        @Size(max = 100, message = "O gênero deve ter no máximo 100 caracteres")
        String genero,

        Integer ano,

        @Size(max = 20, message = "O ISBN deve ter no máximo 20 caracteres")
        String isbn,

        @NotNull(message = "O tipo é obrigatório")
        TipoLivro tipo
) {}