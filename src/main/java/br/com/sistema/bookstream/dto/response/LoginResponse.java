package br.com.sistema.bookstream.dto.response;

public record LoginResponse(
        String token,
        String tipo,
        Long idCliente,
        String login,
        Long expiracaoMs
) {
    public static LoginResponse of(String token, Long idCliente, String login, Long expiracaoMs) {
        return new LoginResponse(token, "Bearer", idCliente, login, expiracaoMs);
    }
}