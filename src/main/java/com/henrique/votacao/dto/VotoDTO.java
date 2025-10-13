package com.henrique.votacao.dto;

import com.henrique.votacao.domain.Escolha;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO de Voto")
public record VotoDTO(
        @Schema(description = "ID do associado") String associadoId,
        @Schema(description = "Escolha do voto") Escolha escolha
) {}
