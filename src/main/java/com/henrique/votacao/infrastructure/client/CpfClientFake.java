package com.henrique.votacao.infrastructure.client;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Random;

@Component
public class CpfClientFake {

    private final Random random;

    public CpfClientFake() {
        this.random = new Random();
    }

    // Construtor
    public CpfClientFake(Random random) {
        this.random = random;
    }

    public Map<String, String> verificarCpf() {
        String status = random.nextBoolean() ? "ABLE_TO_VOTE" : "UNABLE_TO_VOTE";

        return Map.of("status", status);
    }
}
