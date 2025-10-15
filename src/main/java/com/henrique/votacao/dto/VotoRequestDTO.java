package com.henrique.votacao.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Request para registrar votação")
public record VotoRequestDTO(
        @Schema(description = "Título da pauta", example = "Devemos distribuir sacolinhas no Pet Place?")
        @NotBlank(message = "O título da pauta é obrigatório")
        String titulo,

        @Schema(description = "ID do associado", example = "12345678900")
        @NotBlank(message = "O ID do associado é obrigatório")
        @Pattern(regexp = "\\d{11}", message = "O CPF deve conter exatamente 11 números")
        String associadoId,

        @Schema(description = "Escolha do voto (SIM/NAO)", example = "SIM")
        @NotBlank(message = "A escolha do voto é obrigatória")
        @Pattern(regexp = "SIM|NAO", flags = Pattern.Flag.CASE_INSENSITIVE,
                message = "A escolha deve ser 'SIM' ou 'NAO'")
        String escolha
) {}
