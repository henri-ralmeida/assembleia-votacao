package com.henrique.votacao.domain;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Escolha do voto")
public enum Escolha {
    SIM,
    NAO
}
