package br.com.sistema.bookstream.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.InputStream;
import java.time.Duration;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinioService {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${minio.bucket.nome}")
    private String bucket;

    @Value("${minio.link.expiracao-minutos}")
    private int expiracaoMinutos;

    // ==============================================================
    // realizarUploadArquivo - Faz o upload de um arquivo para o MinIO
    // na pasta informada e retorna a key gerada. A key é composta por
    // pasta + UUID + extensão original para evitar colisões.
    // Ex: "livros/550e8400-e29b-41d4-a716-446655440000.pdf"
    // ==============================================================
    public String realizarUploadArquivo(MultipartFile arquivo, String pasta) {
        String key = gerarKey(pasta, arquivo.getOriginalFilename());
        try {
            garantirBucketExiste();

            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(key)
                            .contentType(arquivo.getContentType())
                            .contentLength(arquivo.getSize())
                            .build(),
                    RequestBody.fromInputStream(arquivo.getInputStream(), arquivo.getSize())
            );

            log.info("Upload concluído: bucket={} key={}", bucket, key);
            return key;
        } catch (Exception e) {
            log.error("Erro ao fazer upload no MinIO: bucket={} key={}", bucket, key, e);
            throw new RuntimeException("Falha ao realizar upload do arquivo no MinIO.", e);
        }
    }

    // ==============================================================
    // gerarLinkTemporario - Gera uma URL presigned temporária para
    // acesso direto ao arquivo no MinIO. O tempo de expiração é
    // configurado via application.properties.
    // ==============================================================
    public String gerarLinkTemporario(String key) {
        try {
            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(expiracaoMinutos))
                    .getObjectRequest(GetObjectRequest.builder()
                            .bucket(bucket)
                            .key(key)
                            .build())
                    .build();

            return s3Presigner.presignGetObject(presignRequest).url().toString();
        } catch (Exception e) {
            log.error("Erro ao gerar link temporário: bucket={} key={}", bucket, key, e);
            throw new RuntimeException("Falha ao gerar link temporário.", e);
        }
    }

    // ==============================================================
    // obterStream - Retorna um InputStream do arquivo armazenado no
    // MinIO. Utilizado para leitura direta do conteúdo pelo servidor.
    // ==============================================================
    public InputStream obterStream(String key) {
        try {
            return s3Client.getObject(
                    GetObjectRequest.builder()
                            .bucket(bucket)
                            .key(key)
                            .build()
            );
        } catch (Exception e) {
            log.error("Erro ao obter stream: bucket={} key={}", bucket, key, e);
            throw new RuntimeException("Falha ao obter stream do arquivo.", e);
        }
    }

    // ==============================================================
    // deletarArquivo - Remove um objeto do MinIO pela key.
    // Utilizado ao excluir ou substituir arquivos de um livro.
    // ==============================================================
    public void deletarArquivo(String key) {
        try {
            s3Client.deleteObject(
                    DeleteObjectRequest.builder()
                            .bucket(bucket)
                            .key(key)
                            .build()
            );
            log.info("Arquivo deletado: bucket={} key={}", bucket, key);
        } catch (Exception e) {
            log.error("Erro ao deletar arquivo: bucket={} key={}", bucket, key, e);
            throw new RuntimeException("Falha ao deletar arquivo do MinIO.", e);
        }
    }

    // ==============================================================
    // getBucket - Retorna o nome do bucket configurado no
    // application.properties. Utilizado pelos services de negócio.
    // ==============================================================
    public String getBucket() {
        return bucket;
    }

    // ==============================================================
    // getExpiracaoMinutos - Retorna o tempo de expiração configurado
    // para ser usado na montagem do response com a data de expiração.
    // ==============================================================
    public int getExpiracaoMinutos() {
        return expiracaoMinutos;
    }

    // ==============================================================
    // garantirBucketExiste - Verifica se o bucket existe no MinIO e
    // o cria caso não exista. Chamado antes de qualquer upload.
    // ==============================================================
    private void garantirBucketExiste() {
        try {
            s3Client.headBucket(HeadBucketRequest.builder().bucket(bucket).build());
        } catch (NoSuchBucketException e) {
            s3Client.createBucket(CreateBucketRequest.builder().bucket(bucket).build());
            log.info("Bucket criado: {}", bucket);
        }
    }

    // ==============================================================
    // gerarKey - Gera uma key única combinando pasta, UUID e extensão
    // original do arquivo para evitar colisões de nomes no MinIO.
    // ==============================================================
    private String gerarKey(String pasta, String nomeOriginal) {
        String extensao = "";
        if (nomeOriginal != null && nomeOriginal.contains(".")) {
            extensao = nomeOriginal.substring(nomeOriginal.lastIndexOf("."));
        }
        return pasta + "/" + UUID.randomUUID() + extensao;
    }
}