package com.henrique.votacao.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request para contabilizar resultados das votações de uma pauta")
public record ResultadoVotacaoResponseDTO(

        @Schema(description = "Título da pauta", example = "Devemos distribuir sacolinhas no Pet Place?")
        String tituloPauta,

        @Schema(description = "Resultado da vo do votação")
        ResultadoDTO resultado

) {
    @Schema(description = "Resultado das votações")
    public record ResultadoDTO(
            @Schema(description = "Quantidade de 'SIM'", example = "12")
            double sim,

            @Schema(description = "Quantidade de 'NAO'", example = "6")
            double nao,

            @Schema(description = "Status da votação", example = "APROVADA")
            String status
    ) {}
}
