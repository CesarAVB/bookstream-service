package br.com.sistema.bookstream.dto.request;

import br.com.sistema.bookstream.entity.enums.StatusLivro;
import br.com.sistema.bookstream.entity.enums.TipoLivro;

public record LivroFiltroRequest(
        String nome,
        String autor,
        String genero,
        TipoLivro tipo,
        StatusLivro status,
        Integer anoDe,
        Integer anoAte
) {}