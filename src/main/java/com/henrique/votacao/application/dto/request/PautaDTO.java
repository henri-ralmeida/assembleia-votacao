package com.henrique.votacao.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;

@Schema(description = "DTO para criar uma nova pauta")
public record PautaDTO(
        @NotBlank(message = "O título é obrigatório")
        @Schema(description = "Título da pauta", example = "Devemos distribuir sacolinhas no Pet Place?")
        String tituloPauta
) {}
