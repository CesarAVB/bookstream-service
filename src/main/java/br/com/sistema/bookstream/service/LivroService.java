package br.com.sistema.bookstream.service;

import br.com.sistema.bookstream.dto.request.LivroAtualizacaoRequest;
import br.com.sistema.bookstream.dto.request.LivroCadastroRequest;
import br.com.sistema.bookstream.dto.request.LivroFiltroRequest;
import br.com.sistema.bookstream.dto.response.LivroLinkTemporarioResponse;
import br.com.sistema.bookstream.dto.response.LivroResponse;
import br.com.sistema.bookstream.dto.response.LivroResumoResponse;
import br.com.sistema.bookstream.dto.response.LivroUploadResponse;
import br.com.sistema.bookstream.entity.Livro;
import br.com.sistema.bookstream.entity.enums.StatusLivro;
import br.com.sistema.bookstream.entity.enums.TipoLivro;
import br.com.sistema.bookstream.repository.LivroRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class LivroService {

    private final LivroRepository livroRepository;
    private final MinioService minioService;

    private static final String PASTA_LIVROS = "livros";
    private static final String PASTA_CAPAS  = "capas";

    // ==============================================================
    // cadastrar - Persiste os metadados do livro com status PROCESSANDO.
    // O upload do arquivo é feito em etapa separada via uploadArquivo().
    // Valida duplicidade de ISBN antes de salvar.
    // ==============================================================
    @Transactional
    public LivroResponse cadastrar(LivroCadastroRequest request) {
        if (request.isbn() != null && livroRepository.findByIsbn(request.isbn()).isPresent()) {
            throw new IllegalArgumentException("Já existe um livro cadastrado com o ISBN informado.");
        }

        Livro livro = Livro.builder()
                .nome(request.nome())
                .autor(request.autor())
                .descricao(request.descricao())
                .genero(request.genero())
                .ano(request.ano())
                .isbn(request.isbn())
                .tipo(request.tipo())
                .arquivoBucket("pendente")
                .arquivoKey("pendente")
                .status(StatusLivro.PROCESSANDO)
                .build();

        return LivroResponse.from(livroRepository.save(livro));
    }

    // ==============================================================
    // uploadArquivo - Realiza o upload do arquivo principal (PDF, EPUB
    // ou Audiobook) para a pasta "livros" no MinIO e atualiza a key,
    // tamanho e status do livro para ATIVO após a conclusão.
    // ==============================================================
    @Transactional
    public LivroUploadResponse uploadArquivo(Long id, MultipartFile arquivo) {
        Livro livro = buscarEntidadeOuLancarErro(id);

        String key = minioService.realizarUploadArquivo(arquivo, PASTA_LIVROS);

        livroRepository.atualizarArquivo(id, minioService.getBucket(), key, arquivo.getSize(), StatusLivro.ATIVO);

        return new LivroUploadResponse(
                livro.getId(),
                livro.getNome(),
                livro.getTipo(),
                arquivo.getSize(),
                livro.getDuracaoSegundos(),
                StatusLivro.ATIVO
        );
    }

    // ==============================================================
    // uploadCapa - Realiza o upload da imagem de capa para a pasta
    // "capas" no MinIO e atualiza os campos capaBucket e capaKey.
    // Deleta a capa anterior do MinIO antes de fazer o novo upload.
    // ==============================================================
    @Transactional
    public void uploadCapa(Long id, MultipartFile capa) {
        Livro livro = buscarEntidadeOuLancarErro(id);

        if (livro.getCapaKey() != null) {
            minioService.deletarArquivo(livro.getCapaKey());
        }

        String key = minioService.realizarUploadArquivo(capa, PASTA_CAPAS);

        livroRepository.atualizarCapa(id, minioService.getBucket(), key);
        log.info("Capa atualizada para o livro id={}", id);
    }

    // ==============================================================
    // buscarPorId - Retorna os detalhes completos de um livro pelo id.
    // Lança EntityNotFoundException caso o livro não seja encontrado.
    // ==============================================================
    public LivroResponse buscarPorId(Long id) {
        return LivroResponse.from(buscarEntidadeOuLancarErro(id));
    }

    // ==============================================================
    // listar - Retorna uma página de livros resumidos aplicando os
    // filtros opcionais recebidos no request. A URL da capa é gerada
    // via link temporário do MinIO para cada livro da página.
    // ==============================================================
    public Page<LivroResumoResponse> listar(LivroFiltroRequest filtro, Pageable pageable) {
        Page<Livro> pagina = livroRepository.buscarComFiltros(
                filtro.nome(),
                filtro.autor(),
                filtro.genero(),
                filtro.tipo(),
                filtro.status(),
                filtro.anoDe(),
                filtro.anoAte(),
                pageable
        );

        return pagina.map(livro -> {
            String capaUrl = livro.getCapaKey() != null
                    ? minioService.gerarLinkTemporario(livro.getCapaKey())
                    : null;
            return LivroResumoResponse.from(livro, capaUrl);
        });
    }

    // ==============================================================
    // gerarLinkTemporario - Gera uma URL presigned para acesso ao
    // arquivo principal do livro (PDF ou EPUB). O tempo de expiração
    // é configurável via application.properties. Só funciona para
    // livros com status ATIVO.
    // ==============================================================
    public LivroLinkTemporarioResponse gerarLinkTemporario(Long id) {
        Livro livro = buscarEntidadeOuLancarErro(id);
        validarLivroAtivo(livro);

        String url      = minioService.gerarLinkTemporario(livro.getArquivoKey());
        LocalDateTime expiraEm = LocalDateTime.now().plusMinutes(minioService.getExpiracaoMinutos());

        return new LivroLinkTemporarioResponse(id, url, expiraEm);
    }

    // ==============================================================
    // gerarLinkTemporarioCapa - Gera uma URL presigned para acesso à
    // imagem de capa do livro. Lança erro se o livro não tiver capa.
    // ==============================================================
    public LivroLinkTemporarioResponse gerarLinkTemporarioCapa(Long id) {
        Livro livro = buscarEntidadeOuLancarErro(id);

        if (livro.getCapaKey() == null) {
            throw new IllegalStateException("O livro não possui capa cadastrada.");
        }

        String url      = minioService.gerarLinkTemporario(livro.getCapaKey());
        LocalDateTime expiraEm = LocalDateTime.now().plusMinutes(minioService.getExpiracaoMinutos());

        return new LivroLinkTemporarioResponse(id, url, expiraEm);
    }

    // ==============================================================
    // gerarLinkStreaming - Gera uma URL presigned para streaming de
    // áudio de um Audiobook via redirect. Valida se o tipo do livro
    // é AUDIOBOOK antes de gerar o link.
    // ==============================================================
    public LivroLinkTemporarioResponse gerarLinkStreaming(Long id) {
        Livro livro = buscarEntidadeOuLancarErro(id);
        validarLivroAtivo(livro);

        if (livro.getTipo() != TipoLivro.AUDIOBOOK) {
            throw new IllegalStateException("O livro informado não é um Audiobook.");
        }

        String url      = minioService.gerarLinkTemporario(livro.getArquivoKey());
        LocalDateTime expiraEm = LocalDateTime.now().plusMinutes(minioService.getExpiracaoMinutos());

        return new LivroLinkTemporarioResponse(id, url, expiraEm);
    }

    // ==============================================================
    // atualizar - Atualiza os metadados de um livro. Apenas campos
    // não nulos no request são aplicados (atualização parcial).
    // Valida duplicidade de ISBN caso seja informado.
    // ==============================================================
    @Transactional
    public LivroResponse atualizar(Long id, LivroAtualizacaoRequest request) {
        Livro livro = buscarEntidadeOuLancarErro(id);

        if (request.isbn() != null && livroRepository.existsByIsbnAndIdNot(request.isbn(), id)) {
            throw new IllegalArgumentException("Já existe outro livro cadastrado com o ISBN informado.");
        }

        if (request.nome()      != null) livro.setNome(request.nome());
        if (request.autor()     != null) livro.setAutor(request.autor());
        if (request.descricao() != null) livro.setDescricao(request.descricao());
        if (request.genero()    != null) livro.setGenero(request.genero());
        if (request.ano()       != null) livro.setAno(request.ano());
        if (request.isbn()      != null) livro.setIsbn(request.isbn());

        return LivroResponse.from(livroRepository.save(livro));
    }

    // ==============================================================
    // atualizarStatus - Atualiza apenas o status do livro. Útil para
    // ativar, inativar ou colocar em processamento manualmente.
    // ==============================================================
    @Transactional
    public void atualizarStatus(Long id, StatusLivro status) {
        buscarEntidadeOuLancarErro(id);
        livroRepository.atualizarStatus(id, status);
        log.info("Status do livro id={} atualizado para {}", id, status);
    }

    // ==============================================================
    // deletar - Remove o livro do banco e seus arquivos do MinIO.
    // Deleta o arquivo principal e a capa (se existir).
    // ==============================================================
    @Transactional
    public void deletar(Long id) {
        Livro livro = buscarEntidadeOuLancarErro(id);

        minioService.deletarArquivo(livro.getArquivoKey());

        if (livro.getCapaKey() != null) {
            minioService.deletarArquivo(livro.getCapaKey());
        }

        livroRepository.deleteById(id);
        log.info("Livro deletado: id={}", id);
    }

    // ==============================================================
    // buscarEntidadeOuLancarErro - Método auxiliar que busca o livro
    // pelo id e lança EntityNotFoundException caso não seja encontrado.
    // Centraliza a busca evitando repetição nos demais métodos.
    // ==============================================================
    private Livro buscarEntidadeOuLancarErro(Long id) {
        return livroRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Livro não encontrado com id: " + id));
    }

    // ==============================================================
    // validarLivroAtivo - Valida se o livro está com status ATIVO.
    // Lança IllegalStateException caso esteja inativo ou em processamento.
    // ==============================================================
    private void validarLivroAtivo(Livro livro) {
        if (livro.getStatus() != StatusLivro.ATIVO) {
            throw new IllegalStateException(
                    "O livro não está disponível. Status atual: " + livro.getStatus()
            );
        }
    }
}