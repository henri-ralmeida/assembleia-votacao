package com.henrique.votacao.domain;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Escolha do voto")
public enum Escolha {
    SIM,
    NAO;

    public static Escolha fromString(String valor) {
        if (valor == null) throw new IllegalArgumentException("Escolha não pode ser nula");
        try {
            return valueOf(valor.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Escolha inválida: " + valor);
        }
    }
}
