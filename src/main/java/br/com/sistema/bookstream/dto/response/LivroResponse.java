package br.com.sistema.bookstream.dto.response;

import java.time.LocalDateTime;

import br.com.sistema.bookstream.entity.Livro;
import br.com.sistema.bookstream.entity.enums.StatusLivro;
import br.com.sistema.bookstream.entity.enums.TipoLivro;

public record LivroResponse(
        Long id,
        String nome,
        String autor,
        String descricao,
        String genero,
        Integer ano,
        String isbn,
        TipoLivro tipo,
        Long tamanhoBytes,
        Integer duracaoSegundos,
        StatusLivro status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static LivroResponse from(Livro livro) {
        return new LivroResponse(
                livro.getId(),
                livro.getNome(),
                livro.getAutor(),
                livro.getDescricao(),
                livro.getGenero(),
                livro.getAno(),
                livro.getIsbn(),
                livro.getTipo(),
                livro.getTamanhoBytes(),
                livro.getDuracaoSegundos(),
                livro.getStatus(),
                livro.getCreatedAt(),
                livro.getUpdatedAt()
        );
    }
}