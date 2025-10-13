package com.henrique.votacao.domain;

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
