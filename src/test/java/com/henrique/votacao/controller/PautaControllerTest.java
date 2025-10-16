package com.henrique.votacao.controller;

import com.henrique.votacao.domain.Escolha;
import com.henrique.votacao.domain.Pauta;
import com.henrique.votacao.domain.Voto;
import com.henrique.votacao.dto.AbrirSessaoRequestDTO;
import com.henrique.votacao.dto.PautaRequestDTO;
import com.henrique.votacao.dto.VotoRequestDTO;
import com.henrique.votacao.service.PautaService;
import com.henrique.votacao.service.VotoService;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class PautaControllerTest {

    private PautaService pautaService;
    private VotoService votoService;
    private PautaController pautaController;

    @BeforeEach
    void setUp() {
        pautaService = Mockito.mock(PautaService.class);
        votoService = Mockito.mock(VotoService.class);
        pautaController = new PautaController(pautaService, votoService);
    }

    //region @Test - Criar Pauta
    @Test
    void criarPauta_deveRetornar201_quandoSucesso() {
        // ARRANGE
        PautaRequestDTO request = new PautaRequestDTO("Nova Pauta");
        Pauta criada = new Pauta();
        criada.setId(1L);
        criada.setTitulo(request.titulo());

        when(pautaService.criarPauta(any(Pauta.class))).thenReturn(criada);

        // ACT
        ResponseEntity<PautaRequestDTO> response = pautaController.criarPauta(request);

        // ASSERT
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        assertEquals(request.titulo(), response.getBody().titulo());
        verify(pautaService, times(1)).criarPauta(any(Pauta.class));
    }

    @Test
    void criarPauta_deveRetornar400_quandoTituloVazio() {
        // ARRANGE
        PautaRequestDTO request = new PautaRequestDTO("");
        PautaService pautaServiceMock = mock(PautaService.class);
        when(pautaServiceMock.criarPauta(any()))
                .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Título é obrigatório"));

        PautaController controller = new PautaController(pautaServiceMock, votoService);

        // ACT & ASSERT
        ResponseStatusException exception = Assertions.assertThrows(
                ResponseStatusException.class,
                () -> controller.criarPauta(request)
        );
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    @Test
    void criarPauta_deveRetornar409_quandoTituloDuplicado() {
        // ARRANGE
        PautaRequestDTO request = new PautaRequestDTO("Duplicada");
        when(pautaService.criarPauta(any(Pauta.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.CONFLICT));

        // ACT & ASSERT
        try {
            pautaController.criarPauta(request);
        } catch (ResponseStatusException ex) {
            assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        }
    }
    //endregion

    //region @Test - Abrir Sessao
    @Test
    void abrirSessao_deveRetornar200_quandoSucesso() {
        // ARRANGE
        String titulo = "Pauta Teste";
        Pauta pauta = new Pauta();
        pauta.setTitulo(titulo);
        pauta.setId(1L);

        when(pautaService.buscarPorTitulo(titulo)).thenReturn(Optional.of(pauta));
        when(pautaService.abrirSessao(eq(titulo), anyInt())).thenReturn(pauta);

        AbrirSessaoRequestDTO request = new AbrirSessaoRequestDTO(5);

        // ACT
        ResponseEntity<String> response = pautaController.abrirSessao(titulo, request);

        // ASSERT
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(pautaService, times(1)).abrirSessao(eq(titulo), eq(5));
    }

    @Test
    void abrirSessao_deveRetornar404_quandoPautaNaoEncontrada() {
        // ARRANGE
        String titulo = "Inexistente";
        when(pautaService.buscarPorTitulo(titulo)).thenReturn(Optional.empty());

        AbrirSessaoRequestDTO request = new AbrirSessaoRequestDTO(5);

        // ACT & ASSERT
        try {
            pautaController.abrirSessao(titulo, request);
        } catch (ResponseStatusException ex) {
            assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        }
    }
    //endregion

    //region @Test - Votar
    @Test
    void votar_deveRetornar201_quandoSucesso() {
        // ARRANGE
        String titulo = "Pauta Teste";
        String associadoId = "12345678900";
        String escolha = "SIM";

        VotoRequestDTO request = new VotoRequestDTO(associadoId, escolha);

        Voto voto = new Voto();
        voto.setAssociadoId(associadoId);
        voto.setEscolha(Escolha.SIM);

        when(votoService.registrarVotoPorTitulo(titulo, associadoId, escolha)).thenReturn(voto);

        // ACT
        ResponseEntity<VotoRequestDTO> response = pautaController.votar(titulo, request);

        // ASSERT
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        assertEquals(associadoId, response.getBody().associadoId());
        assertEquals(escolha, response.getBody().escolha());
        verify(votoService, times(1)).registrarVotoPorTitulo(titulo, associadoId, escolha);
    }

    @Test
    void votar_deveRetornar401_quandoNaoAutorizado() {
        // ARRANGE
        String titulo = "Pauta Teste";
        String associadoId = "00000000000";
        String escolha = "SIM";

        VotoRequestDTO request = new VotoRequestDTO(associadoId, escolha);

        when(votoService.registrarVotoPorTitulo(titulo, associadoId, escolha))
                .thenThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        // ACT & ASSERT
        try {
            pautaController.votar(titulo, request);
        } catch (ResponseStatusException ex) {
            assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
        }
    }

    @Test
    void votar_deveRetornar404_quandoPautaNaoEncontrada() {
        // ARRANGE
        String titulo = "NaoExiste";
        String associadoId = "12345678900";
        String escolha = "SIM";

        VotoRequestDTO request = new VotoRequestDTO(associadoId, escolha);

        // ACT
        when(votoService.registrarVotoPorTitulo(titulo, associadoId, escolha))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        // ASSERT
        try {
            pautaController.votar(titulo, request);
        } catch (ResponseStatusException ex) {
            assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        }
    }

    @Test
    void votar_deveRetornar409_quandoAssociadoJaVotou() {
        // ARRANGE
        String titulo = "Pauta Teste";
        String associadoId = "12345678900";
        String escolha = "SIM";

        VotoRequestDTO request = new VotoRequestDTO(associadoId, escolha);

        // ACT
        when(votoService.registrarVotoPorTitulo(titulo, associadoId, escolha))
                .thenThrow(new ResponseStatusException(HttpStatus.CONFLICT));

        // ASSERT
        try {
            pautaController.votar(titulo, request);
        } catch (ResponseStatusException ex) {
            assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        }
    }
    //endregion

    //region @Test - Resultado
    @Test
    void resultado_deveRetornar200_quandoSucesso() {
        // ARRANGE
        String titulo = "Pauta Teste";
        String resultadoEsperado = "100% SIM, 0% NAO, APROVADA";

        when(votoService.calcularResultadoPorTitulo(titulo)).thenReturn(resultadoEsperado);

        // ACT
        ResponseEntity<String> response = pautaController.resultado(titulo);

        // ASSERT
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(resultadoEsperado, response.getBody());
        verify(votoService, times(1)).calcularResultadoPorTitulo(titulo);
    }

    @Test
    void resultado_deveRetornar404_quandoPautaNaoEncontrada() {
        // ARRANGE
        String titulo = "NaoExiste";

        // ACT
        when(votoService.calcularResultadoPorTitulo(titulo))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        // ASSERT
        try {
            pautaController.resultado(titulo);
        } catch (ResponseStatusException ex) {
            assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        }
    }
    //endregion
}
