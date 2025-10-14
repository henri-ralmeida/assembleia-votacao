package com.henrique.votacao.client;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Component
public class CpfClientFake {

    private final Random random = new Random();

    public Map<String, String> verificarCpf(String cpf) {
        // 50% chance de CPF ser inválido
        if (random.nextBoolean()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "CPF inválido");
        }

        // 50% chance de poder ou não votar
        Map<String, String> response = new HashMap<>();
        response.put("status", random.nextBoolean() ? "ABLE_TO_VOTE" : "UNABLE_TO_VOTE");
        return response;
    }
}
