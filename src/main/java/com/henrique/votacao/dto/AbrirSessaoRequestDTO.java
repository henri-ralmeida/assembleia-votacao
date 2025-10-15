package com.henrique.votacao.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request para abrir sessão de votação")
public record AbrirSessaoRequestDTO(
        @NotBlank(message = "O título da pauta é obrigatório")
        @Schema(description = "Título da pauta para abrir sessão", example = "Devemos distribuir sacolinhas no Pet Place?")
        String titulo,

        @Schema(description = "Duração da sessão em minutos (opcional, default = 1)", example = "1")
        Integer duracaoMinutos
) {}
