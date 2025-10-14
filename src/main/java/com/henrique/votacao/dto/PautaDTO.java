package com.henrique.votacao.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "DTO de Pauta")
public record PautaDTO(
        @Schema(description = "ID da pauta") Long id,

        @NotBlank(message = "O título é obrigatório")
        @Schema(description = "Título da pauta") String titulo
) {}
