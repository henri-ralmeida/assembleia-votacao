package com.henrique.votacao.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO de Pauta")
public record PautaDTO(
        @Schema(description = "ID da pauta") Long id,
        @Schema(description = "Título da pauta") String titulo
) {}
