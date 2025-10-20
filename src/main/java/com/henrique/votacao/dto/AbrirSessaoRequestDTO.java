package com.henrique.votacao.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request para abrir sessão de votação")
public record AbrirSessaoRequestDTO(
        @Schema(description = "Duração da sessão em minutos (opcional, default = 1)", example = "1")
        Integer duracaoMinutos
) {}
