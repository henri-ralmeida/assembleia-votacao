package com.henrique.votacao.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response para abrir sessão de votação")
public record AbrirSessaoResponseDTO(
        @Schema(description = "Mensagem de aviso para a sessão aberta",
                example = "Sessão de votação aberta por 1 minuto(s) para a pauta: 'Devemos distribuir sacolinhas no Pet Place?'")
        String mensagem
) {
    public static AbrirSessaoResponseDTO criarMensagem(String titulo, Integer duracaoMinutos) {
        String mensagem = "Sessão de votação aberta por " + duracaoMinutos + " minuto(s) para a pauta: '" + titulo + "'";
        return new AbrirSessaoResponseDTO(mensagem);
    }
}
