package br.com.sistema.bookstream.dto.response;

import java.time.LocalDateTime;

public record LivroLinkTemporarioResponse(
        Long livroId,
        String url,
        LocalDateTime expiraEm
) {}