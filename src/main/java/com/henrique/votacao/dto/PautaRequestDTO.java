package com.henrique.votacao.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request para criar uma nova pauta")
public record PautaRequestDTO(
        @NotBlank(message = "O título é obrigatório")
        @Schema(description = "Título da pauta", example = "Devemos distribuir sacolinhas no Pet Place?")
        String titulo
) {}
