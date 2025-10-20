package com.henrique.votacao.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response para registrar votação")
public record VotoResponseDTO(
        @Schema(description = "Mensagem de aviso para a contagem de voto",
                example = "Voto 'SIM' registrado com sucesso para pauta 'Devemos distribuir sacolinhas no Pet Place?' no CPF de '12345678900'")
        String mensagem
) {
    public static VotoResponseDTO criarMensagem(String titulo, String cpf, String escolha) {
        String mensagem = "Voto '" + escolha + "' registrado com sucesso para pauta '" + titulo + "' no CPF de '" + cpf + "'";
        return new VotoResponseDTO(mensagem);
    }
}