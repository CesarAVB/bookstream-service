package br.com.sistema.bookstream.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.sistema.bookstream.entity.Livro;
import br.com.sistema.bookstream.entity.enums.StatusLivro;
import br.com.sistema.bookstream.entity.enums.TipoLivro;

@Repository
public interface LivroRepository extends JpaRepository<Livro, Long>, JpaSpecificationExecutor<Livro> {

    // Busca por nome contendo o termo (case-insensitive)
    Page<Livro> findByNomeContainingIgnoreCase(String nome, Pageable pageable);

    // Busca por autor contendo o termo (case-insensitive)
    Page<Livro> findByAutorContainingIgnoreCase(String autor, Pageable pageable);

    // Busca por gênero exato e status
    Page<Livro> findByGeneroAndStatus(String genero, StatusLivro status, Pageable pageable);

    // Busca por tipo e status
    Page<Livro> findByTipoAndStatus(TipoLivro tipo, StatusLivro status, Pageable pageable);

    // Busca por status
    Page<Livro> findByStatus(StatusLivro status, Pageable pageable);

    // Busca por ISBN
    Optional<Livro> findByIsbn(String isbn);

    // Verifica existência por ISBN ignorando um id (útil para validação de duplicidade)
    boolean existsByIsbnAndIdNot(String isbn, Long id);

    // Busca com filtros combinados via JPQL
    @Query("""
            SELECT l FROM Livro l
            WHERE (:nome    IS NULL OR LOWER(l.nome)   LIKE LOWER(CONCAT('%', :nome,   '%')))
            AND   (:autor   IS NULL OR LOWER(l.autor)  LIKE LOWER(CONCAT('%', :autor,  '%')))
            AND   (:genero  IS NULL OR LOWER(l.genero) LIKE LOWER(CONCAT('%', :genero, '%')))
            AND   (:tipo    IS NULL OR l.tipo   = :tipo)
            AND   (:status  IS NULL OR l.status = :status)
            AND   (:anoDe   IS NULL OR l.ano   >= :anoDe)
            AND   (:anoAte  IS NULL OR l.ano   <= :anoAte)
            """)
    Page<Livro> buscarComFiltros(
            @Param("nome")   String nome,
            @Param("autor")  String autor,
            @Param("genero") String genero,
            @Param("tipo")   TipoLivro tipo,
            @Param("status") StatusLivro status,
            @Param("anoDe")  Integer anoDe,
            @Param("anoAte") Integer anoAte,
            Pageable pageable
    );

    // Atualiza apenas o status do livro
    @Modifying
    @Query("UPDATE Livro l SET l.status = :status WHERE l.id = :id")
    int atualizarStatus(@Param("id") Long id, @Param("status") StatusLivro status);

    // Atualiza os dados do arquivo principal após upload no MinIO
    @Modifying
    @Query("""
            UPDATE Livro l
            SET l.arquivoBucket = :bucket,
                l.arquivoKey    = :key,
                l.tamanhoBytes  = :tamanho,
                l.status        = :status
            WHERE l.id = :id
            """)
    int atualizarArquivo(
            @Param("id")      Long id,
            @Param("bucket")  String bucket,
            @Param("key")     String key,
            @Param("tamanho") Long tamanho,
            @Param("status")  StatusLivro status
    );

    // Atualiza os dados da capa após upload no MinIO
    @Modifying
    @Query("""
            UPDATE Livro l
            SET l.capaBucket = :bucket,
                l.capaKey    = :key
            WHERE l.id = :id
            """)
    int atualizarCapa(
            @Param("id")     Long id,
            @Param("bucket") String bucket,
            @Param("key")    String key
    );
}