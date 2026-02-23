package br.com.sistema.bookstream.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import br.com.sistema.bookstream.entity.enums.StatusLivro;
import br.com.sistema.bookstream.entity.enums.TipoLivro;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "livros", indexes = {
        @Index(name = "idx_nome",   columnList = "nome"),
        @Index(name = "idx_autor",  columnList = "autor"),
        @Index(name = "idx_genero", columnList = "genero"),
        @Index(name = "idx_tipo",   columnList = "tipo"),
        @Index(name = "idx_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Livro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // -------------------------------------------------------
    // Informações básicas
    // -------------------------------------------------------

    @Column(nullable = false, length = 255)
    private String nome;

    @Column(length = 255)
    private String autor;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(length = 100)
    private String genero;

    @Column(columnDefinition = "YEAR")
    private Integer ano;

    @Column(length = 20)
    private String isbn;

    // -------------------------------------------------------
    // Tipo do conteúdo
    // -------------------------------------------------------

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoLivro tipo;

    // -------------------------------------------------------
    // Arquivo principal (MinIO / S3)
    // -------------------------------------------------------

    @Column(name = "arquivo_bucket", nullable = false, length = 100)
    private String arquivoBucket;

    @Column(name = "arquivo_key", nullable = false, length = 500)
    private String arquivoKey;

    @Column(name = "tamanho_bytes")
    private Long tamanhoBytes;

    // -------------------------------------------------------
    // Capa do livro
    // -------------------------------------------------------

    @Column(name = "capa_bucket", length = 100)
    private String capaBucket;

    @Column(name = "capa_key", length = 500)
    private String capaKey;

    // -------------------------------------------------------
    // Apenas para audiobook
    // -------------------------------------------------------

    @Column(name = "duracao_segundos")
    private Integer duracaoSegundos;

    // -------------------------------------------------------
    // Controle de status
    // -------------------------------------------------------

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private StatusLivro status = StatusLivro.PROCESSANDO;

    // -------------------------------------------------------
    // Auditoria
    // -------------------------------------------------------

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

}