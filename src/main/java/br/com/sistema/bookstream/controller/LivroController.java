package br.com.sistema.bookstream.controller;

import br.com.sistema.bookstream.dto.request.LivroAtualizacaoRequest;
import br.com.sistema.bookstream.dto.request.LivroCadastroRequest;
import br.com.sistema.bookstream.dto.request.LivroFiltroRequest;
import br.com.sistema.bookstream.dto.response.LivroLinkTemporarioResponse;
import br.com.sistema.bookstream.dto.response.LivroResponse;
import br.com.sistema.bookstream.dto.response.LivroResumoResponse;
import br.com.sistema.bookstream.dto.response.LivroUploadResponse;
import br.com.sistema.bookstream.entity.enums.StatusLivro;
import br.com.sistema.bookstream.service.LivroService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/livros")
@RequiredArgsConstructor
public class LivroController {

    private final LivroService livroService;

    // ==============================================================
    // cadastrar - Recebe os metadados do livro e persiste com status
    // PROCESSANDO. O upload do arquivo deve ser feito em seguida
    // via endpoint POST /{id}/arquivo.
    // ==============================================================
    @PostMapping
    public ResponseEntity<LivroResponse> cadastrar(@RequestBody @Valid LivroCadastroRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(livroService.cadastrar(request));
    }

    // ==============================================================
    // uploadArquivo - Recebe o arquivo principal do livro (PDF, EPUB
    // ou Audiobook) via multipart e realiza o upload no MinIO.
    // Atualiza o status do livro para ATIVO após a conclusão.
    // ==============================================================
    @PostMapping(value = "/{id}/arquivo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<LivroUploadResponse> uploadArquivo(
            @PathVariable Long id,
            @RequestPart("arquivo") MultipartFile arquivo) {
        return ResponseEntity.ok(livroService.uploadArquivo(id, arquivo));
    }

    // ==============================================================
    // uploadCapa - Recebe a imagem de capa do livro via multipart e
    // realiza o upload no MinIO. Substitui a capa anterior se existir.
    // ==============================================================
    @PostMapping(value = "/{id}/capa", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadCapa(
            @PathVariable Long id,
            @RequestPart("capa") MultipartFile capa) {
        livroService.uploadCapa(id, capa);
        return ResponseEntity.noContent().build();
    }

    // ==============================================================
    // buscarPorId - Retorna os detalhes completos de um livro pelo id.
    // ==============================================================
    @GetMapping("/{id}")
    public ResponseEntity<LivroResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(livroService.buscarPorId(id));
    }

    // ==============================================================
    // listar - Retorna uma página de livros resumidos com suporte a
    // filtros opcionais (nome, autor, gênero, tipo, status, intervalo
    // de ano) e paginação via Pageable. Padrão: 20 itens por página
    // ordenados por nome.
    // ==============================================================
    @GetMapping
    public ResponseEntity<Page<LivroResumoResponse>> listar(
            @ModelAttribute LivroFiltroRequest filtro,
            @PageableDefault(size = 20, sort = "nome") Pageable pageable) {
        return ResponseEntity.ok(livroService.listar(filtro, pageable));
    }

    // ==============================================================
    // gerarLinkTemporario - Gera e retorna uma URL presigned para
    // acesso ao arquivo principal do livro (PDF ou EPUB). Disponível
    // apenas para livros com status ATIVO.
    // ==============================================================
    @GetMapping("/{id}/link")
    public ResponseEntity<LivroLinkTemporarioResponse> gerarLinkTemporario(@PathVariable Long id) {
        return ResponseEntity.ok(livroService.gerarLinkTemporario(id));
    }

    // ==============================================================
    // gerarLinkTemporarioCapa - Gera e retorna uma URL presigned para
    // acesso à imagem de capa do livro. Retorna erro se não houver
    // capa cadastrada.
    // ==============================================================
    @GetMapping("/{id}/capa/link")
    public ResponseEntity<LivroLinkTemporarioResponse> gerarLinkTemporarioCapa(@PathVariable Long id) {
        return ResponseEntity.ok(livroService.gerarLinkTemporarioCapa(id));
    }

    // ==============================================================
    // gerarLinkStreaming - Gera e retorna uma URL presigned para
    // streaming de áudio. Disponível apenas para livros do tipo
    // AUDIOBOOK com status ATIVO.
    // ==============================================================
    @GetMapping("/{id}/streaming")
    public ResponseEntity<LivroLinkTemporarioResponse> gerarLinkStreaming(@PathVariable Long id) {
        return ResponseEntity.ok(livroService.gerarLinkStreaming(id));
    }

    // ==============================================================
    // atualizar - Atualiza os metadados de um livro. Apenas os campos
    // informados no body são alterados (atualização parcial).
    // ==============================================================
    @PutMapping("/{id}")
    public ResponseEntity<LivroResponse> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid LivroAtualizacaoRequest request) {
        return ResponseEntity.ok(livroService.atualizar(id, request));
    }

    // ==============================================================
    // atualizarStatus - Atualiza apenas o status do livro. Recebe o
    // novo status como parâmetro de query (ATIVO, INATIVO, PROCESSANDO).
    // ==============================================================
    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> atualizarStatus(
            @PathVariable Long id,
            @RequestParam StatusLivro status) {
        livroService.atualizarStatus(id, status);
        return ResponseEntity.noContent().build();
    }

    // ==============================================================
    // deletar - Remove o livro do banco de dados e apaga os arquivos
    // correspondentes (principal e capa) do MinIO.
    // ==============================================================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        livroService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}