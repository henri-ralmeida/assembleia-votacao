package com.henrique.votacao.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.henrique.votacao.client.CpfClientFake;
import com.henrique.votacao.domain.Pauta;
import com.henrique.votacao.dto.VotoRequestDTO;
import com.henrique.votacao.service.PautaService;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Map;
import java.util.stream.IntStream;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
class PautaControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PautaService pautaService;

    @MockitoBean
    private CpfClientFake cpfClient;

    private Pauta pauta;
    private static final int TOTAL_VOTOS = 1000;

    @BeforeEach
    void setup() {
        pauta = new Pauta();
        pauta.setTituloPauta("Pauta Teste " + System.currentTimeMillis());
        pauta = pautaService.criarPauta(pauta);
        pautaService.abrirSessao(pauta.getTituloPauta(), 5);

        when(cpfClient.verificarCpf())
                .thenReturn(Map.of("status", "ABLE_TO_VOTE"));
    }

    @Test
    void votar_cenario200_deveRetornar200() throws Exception {
        // ACCT
        String cpfValido = "12345678901";

        // ARRANGE
        VotoRequestDTO request = new VotoRequestDTO(cpfValido, "SIM");

        // ASSERT
        mockMvc.perform(post("/api/v1/pautas/" + pauta.getTituloPauta() + "/votos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void votar_cenario401_deveRetornar401() throws Exception {
        // ACT
        String cpfInvalido = "00000000000";

        when(cpfClient.verificarCpf())
                .thenReturn(Map.of("status", "UNABLE_TO_VOTE"));

        // ARRANGE
        VotoRequestDTO request = new VotoRequestDTO(cpfInvalido, "SIM");

        // ASSERT
        mockMvc.perform(post("/api/v1/pautas/" + pauta.getTituloPauta() + "/votos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testeVotosMassivos() throws Exception {
        long startTime = System.currentTimeMillis();
        IntStream.range(0, TOTAL_VOTOS).forEach(i -> {
            String cpf = String.format("%011d", i);
            VotoRequestDTO request = new VotoRequestDTO(cpf, "SIM");

            try {
                mockMvc.perform(post("/api/v1/pautas/" + pauta.getTituloPauta() + "/votos")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isCreated());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        long endTime = System.currentTimeMillis();
        System.out.println("Tempo total para " + TOTAL_VOTOS + " votos: " + (endTime - startTime) + "ms");
    }
}
