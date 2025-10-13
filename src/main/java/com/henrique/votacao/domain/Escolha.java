package com.henrique.votacao.domain;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Escolha do voto")
public enum Escolha {
    SIM,
    NAO;

    public static Escolha fromString(String valor) {
        try {
            return Escolha.valueOf(valor.toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("Escolha inválida: " + valor);
        }
    }
}
