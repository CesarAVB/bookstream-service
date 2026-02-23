package br.com.sistema.bookstream.dto.response;

import br.com.sistema.bookstream.entity.Livro;
import br.com.sistema.bookstream.entity.enums.StatusLivro;
import br.com.sistema.bookstream.entity.enums.TipoLivro;

public record LivroResumoResponse(
        Long id,
        String nome,
        String autor,
        String genero,
        Integer ano,
        TipoLivro tipo,
        StatusLivro status,
        String capaUrl
) {
    public static LivroResumoResponse from(Livro livro, String capaUrl) {
        return new LivroResumoResponse(
                livro.getId(),
                livro.getNome(),
                livro.getAutor(),
                livro.getGenero(),
                livro.getAno(),
                livro.getTipo(),
                livro.getStatus(),
                capaUrl
        );
    }
}