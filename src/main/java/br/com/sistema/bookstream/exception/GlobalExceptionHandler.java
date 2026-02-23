package br.com.sistema.bookstream.exception;

import br.com.sistema.bookstream.dto.response.ErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ==============================================================
    // handleEntityNotFoundException - Captura EntityNotFoundException
    // lançada quando um livro não é encontrado pelo id. Retorna 404.
    // ==============================================================
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException ex) {
        log.warn("Recurso não encontrado: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.of(
                        HttpStatus.NOT_FOUND.value(),
                        "Recurso não encontrado",
                        ex.getMessage()
                ));
    }

    // ==============================================================
    // handleIllegalArgumentException - Captura IllegalArgumentException
    // lançada em validações de negócio como ISBN duplicado.
    // Retorna 400.
    // ==============================================================
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("Argumento inválido: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(
                        HttpStatus.BAD_REQUEST.value(),
                        "Requisição inválida",
                        ex.getMessage()
                ));
    }

    // ==============================================================
    // handleIllegalStateException - Captura IllegalStateException
    // lançada quando o livro não está ATIVO ou não é do tipo correto
    // para a operação solicitada (ex: streaming em não-Audiobook).
    // Retorna 422.
    // ==============================================================
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(IllegalStateException ex) {
        log.warn("Estado inválido: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(ErrorResponse.of(
                        HttpStatus.UNPROCESSABLE_ENTITY.value(),
                        "Operação não permitida",
                        ex.getMessage()
                ));
    }

    // ==============================================================
    // handleMethodArgumentNotValidException - Captura erros de validação
    // do Bean Validation (@NotBlank, @Size, etc.) nos DTOs de request.
    // Retorna 400 com a lista de campos inválidos e suas mensagens.
    // ==============================================================
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        List<ErrorResponse.CampoErro> campos = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(field -> new ErrorResponse.CampoErro(field.getField(), field.getDefaultMessage()))
                .toList();

        log.warn("Erro de validação: {} campo(s) inválido(s)", campos.size());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.ofValidacao(
                        HttpStatus.BAD_REQUEST.value(),
                        "Erro de validação",
                        "Um ou mais campos estão inválidos.",
                        campos
                ));
    }

    // ==============================================================
    // handleMaxUploadSizeExceededException - Captura o erro quando o
    // arquivo enviado ultrapassa o tamanho máximo configurado no
    // application.properties (spring.servlet.multipart.max-file-size).
    // Retorna 413.
    // ==============================================================
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex) {
        log.warn("Arquivo excede o tamanho máximo permitido: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(ErrorResponse.of(
                        HttpStatus.PAYLOAD_TOO_LARGE.value(),
                        "Arquivo muito grande",
                        "O arquivo enviado excede o tamanho máximo permitido."
                ));
    }

    // ==============================================================
    // handleRuntimeException - Captura RuntimeException genérica,
    // incluindo falhas de comunicação com o MinIO. Retorna 500.
    // ==============================================================
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
        log.error("Erro interno inesperado: {}", ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.of(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "Erro interno",
                        "Ocorreu um erro inesperado. Tente novamente mais tarde."
                ));
    }
}