package com.henrique.votacao.infrastructure.client;

import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CpfClientFakeTest {

    @Test
    void verificarCpf_deveRetornarABLE_TO_VOTE() {
        // ARRANGE
        Random randomMock = mock(Random.class);
        when(randomMock.nextBoolean()).thenReturn(true);

        CpfClientFake cpfClient = new CpfClientFake(randomMock);

        // ACT
        Map<String, String> resultado = cpfClient.verificarCpf();

        // ASSERT
        assertEquals("ABLE_TO_VOTE", resultado.get("status"));
    }

    @Test
    void verificarCpf_deveRetornarUNABLE_TO_VOTE() {
        // ARRANGE
        Random randomMock = mock(Random.class);
        when(randomMock.nextBoolean()).thenReturn(false);

        CpfClientFake cpfClient = new CpfClientFake(randomMock);

        // ACT
        Map<String, String> resultado = cpfClient.verificarCpf();

        // ASSERT
        assertEquals("UNABLE_TO_VOTE", resultado.get("status"));
    }
}
