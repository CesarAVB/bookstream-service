package br.com.sistema.bookstream.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        int status,
        String erro,
        String mensagem,
        LocalDateTime timestamp,
        List<CampoErro> campos
) {
    public static ErrorResponse of(int status, String erro, String mensagem) {
        return new ErrorResponse(status, erro, mensagem, LocalDateTime.now(), null);
    }

    public static ErrorResponse ofValidacao(int status, String erro, String mensagem, List<CampoErro> campos) {
        return new ErrorResponse(status, erro, mensagem, LocalDateTime.now(), campos);
    }

    public record CampoErro(String campo, String mensagem) {}
}