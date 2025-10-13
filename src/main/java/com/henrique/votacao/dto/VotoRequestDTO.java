package com.henrique.votacao.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request para registrar voto")
public record VotoRequestDTO(
        @Schema(description = "ID do associado", example = "12345678900") String associadoId,
        @Schema(description = "Escolha do voto (SIM/NAO)", example = "SIM") String escolha
) {}
