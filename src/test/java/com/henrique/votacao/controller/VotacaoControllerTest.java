package com.henrique.votacao.controller;

import com.henrique.votacao.domain.Escolha;
import com.henrique.votacao.domain.Voto;
import com.henrique.votacao.dto.VotoRequestDTO;
import com.henrique.votacao.service.PautaService;
import com.henrique.votacao.service.VotoService;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class VotacaoControllerTest {

    private VotoService votoService;
    private VotacaoController votacaoController;

    @BeforeEach
    void setUp() {
        PautaService pautaService = Mockito.mock(PautaService.class);
        votoService = Mockito.mock(VotoService.class);
        votacaoController = new VotacaoController(pautaService, votoService);
    }

    @Test
    void votar_deveChamarServicoERetornar200() {
        // ARRANGE
        String titulo = "Pauta Teste";
        String associadoId = "12345678900";
        String escolha = "SIM";

        VotoRequestDTO request = new VotoRequestDTO(titulo, associadoId, escolha);

        Voto voto = new Voto();
        voto.setEscolha(Escolha.SIM);
        voto.setAssociadoId(associadoId);

        // Mock do serviço
        when(votoService.registrarVotoPorTitulo(titulo, associadoId, escolha)).thenReturn(voto);

        // ACT
        ResponseEntity<VotoRequestDTO> response = votacaoController.votar(request);

        // ASSERT
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        assertEquals(titulo, response.getBody().titulo());
        assertEquals(associadoId, response.getBody().associadoId());
        assertEquals(escolha, response.getBody().escolha());
        verify(votoService, times(1)).registrarVotoPorTitulo(titulo, associadoId, escolha);
    }
}
