package br.com.sistema.bookstream.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import br.com.sistema.bookstream.dto.response.LivroLinkTemporarioResponse;
import br.com.sistema.bookstream.entity.Livro;
import br.com.sistema.bookstream.repository.LivroRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class LivroService {

    private final LivroRepository livroRepository;
    private final MinioService minioService;

    // ====================================
    // URLs públicas do MinIO
    // ====================================
    @Value("${minio.public-endpoint:https://minio-console.cesaravb.com.br}")
    private String minioPublicEndpoint;

    @Value("${minio.bucket-name:logbook}")
    private String bucketName;

    // ====================================
    // gerarLinkTemporario - Gera link presigned com URL pública
    // ====================================
    public LivroLinkTemporarioResponse gerarLinkTemporario(Long id) {
        Livro livro = livroRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Livro não encontrado"));

        if (livro.getArquivoKey() == null) {
            throw new IllegalArgumentException("Livro não possui arquivo");
        }

        // Obter link presigned do MinIO
        String linkPresigned = minioService.gerarLinkTemporario(livro.getArquivoKey());
        
        // ✅ Substituir endpoint interno pelo público
        String linkPublico = substituirEndpointParaPublico(linkPresigned);

        LocalDateTime expiresAt = LocalDateTime.now()
            .plusMinutes(minioService.getExpiracaoMinutos());

        log.info("📸 Link temporário gerado para livro: {}", id);
        log.info("   Link público: {}", linkPublico.substring(0, Math.min(100, linkPublico.length())) + "...");

        // ✅ Usar construtor do record (sem builder)
        return new LivroLinkTemporarioResponse(
            livro.getId(),
            linkPublico,
            expiresAt
        );
    }

    // ====================================
    // gerarLinkTemporarioCapa - Gera link presigned da capa com URL pública
    // ====================================
    public LivroLinkTemporarioResponse gerarLinkTemporarioCapa(Long id) {
        Livro livro = livroRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Livro não encontrado"));

        if (livro.getCapaKey() == null) {
            throw new IllegalArgumentException("Livro não possui capa");
        }

        // Obter link presigned do MinIO
        String linkPresigned = minioService.gerarLinkTemporario(livro.getCapaKey());
        
        // ✅ Substituir endpoint interno pelo público
        String linkPublico = substituirEndpointParaPublico(linkPresigned);

        LocalDateTime expiresAt = LocalDateTime.now()
            .plusMinutes(minioService.getExpiracaoMinutos());

        log.info("📸 Link temporário da capa gerado para livro: {}", id);
        log.info("   Link público: {}", linkPublico.substring(0, Math.min(100, linkPublico.length())) + "...");

        // ✅ Usar construtor do record
        return new LivroLinkTemporarioResponse(
            livro.getId(),
            linkPublico,
            expiresAt
        );
    }

    // ====================================
    // gerarLinkStreaming - Gera link presigned para audiobook
    // ====================================
    public LivroLinkTemporarioResponse gerarLinkStreaming(Long id) {
        Livro livro = livroRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Livro não encontrado"));

        if (livro.getArquivoKey() == null) {
            throw new IllegalArgumentException("Audiobook não possui arquivo");
        }

        // Obter link presigned do MinIO
        String linkPresigned = minioService.gerarLinkTemporario(livro.getArquivoKey());
        
        // ✅ Substituir endpoint interno pelo público
        String linkPublico = substituirEndpointParaPublico(linkPresigned);

        LocalDateTime expiresAt = LocalDateTime.now()
            .plusMinutes(minioService.getExpiracaoMinutos());

        log.info("🎧 Link de streaming gerado para audiobook: {}", id);

        // ✅ Usar construtor do record
        return new LivroLinkTemporarioResponse(
            livro.getId(),
            linkPublico,
            expiresAt
        );
    }

    // ====================================
    // substituirEndpointParaPublico - Substitui endpoint interno pelo público
    // Exemplo: http://minio:9000/bucket/... → https://minio-console.cesaravb.com.br/bucket/...
    // ====================================
    private String substituirEndpointParaPublico(String linkPresigned) {
        // Se já tem o endpoint público, retornar como está
        if (linkPresigned.contains("cesaravb.com.br")) {
            log.debug("✅ Link já tem endpoint público");
            return linkPresigned;
        }

        // Extrair path do link presigned
        // Formato: http://minio:9000/bucket/path/arquivo.jpg?params
        try {
            // Encontrar a posição do bucket
            String bucketPath = "/" + bucketName + "/";
            int bucketIndex = linkPresigned.indexOf(bucketPath);

            if (bucketIndex > 0) {
                // Extrair tudo a partir do bucket
                String pathAndParams = linkPresigned.substring(bucketIndex);
                
                // Montar URL pública
                String publicUrl = minioPublicEndpoint + pathAndParams;
                
                log.debug("🔄 Substituição de endpoint:");
                log.debug("   De: {}", linkPresigned.substring(0, Math.min(80, linkPresigned.length())) + "...");
                log.debug("   Para: {}", publicUrl.substring(0, Math.min(80, publicUrl.length())) + "...");
                
                return publicUrl;
            }
        } catch (Exception e) {
            log.error("❌ Erro ao substituir endpoint: {}", e.getMessage());
        }

        // Se não conseguir converter, retornar o original
        log.warn("⚠️ Não foi possível converter endpoint, retornando original");
        return linkPresigned;
    }
}