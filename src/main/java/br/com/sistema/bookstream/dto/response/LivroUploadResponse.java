package br.com.sistema.bookstream.dto.response;

import br.com.sistema.bookstream.entity.enums.StatusLivro;
import br.com.sistema.bookstream.entity.enums.TipoLivro;

public record LivroUploadResponse(
        Long id,
        String nome,
        TipoLivro tipo,
        Long tamanhoBytes,
        Integer duracaoSegundos,
        StatusLivro status
) {}