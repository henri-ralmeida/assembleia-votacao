package com.henrique.votacao.controller;

import com.henrique.votacao.domain.model.voto.Escolha;
import com.henrique.votacao.domain.model.pauta.Pauta;
import com.henrique.votacao.domain.model.pauta.TituloPauta;
import com.henrique.votacao.domain.model.voto.Voto;
import com.henrique.votacao.domain.model.voto.Cpf;
import com.henrique.votacao.domain.exception.*;
import com.henrique.votacao.application.dto.request.*;
import com.henrique.votacao.application.dto.response.*;
import com.henrique.votacao.service.PautaService;
import com.henrique.votacao.service.VotoService;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
        PautaDTO request = new PautaDTO("Nova Pauta");
        TituloPauta titulo = new TituloPauta("Nova Pauta");
        Pauta criada = new Pauta(titulo);

        when(pautaService.criarPauta(any(Pauta.class))).thenReturn(criada);

        // ACT
        ResponseEntity<PautaDTO> response = pautaController.criarPauta(request);

        // ASSERT
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        assertEquals(request.tituloPauta(), response.getBody().tituloPauta());
        verify(pautaService, times(1)).criarPauta(any(Pauta.class));
    }

    @Test
    void criarPauta_deveRetornar400_quandoTituloVazio() {
        // ARRANGE
        PautaDTO request = new PautaDTO("");
        PautaService pautaServiceMock = mock(PautaService.class);
        when(pautaServiceMock.criarPauta(any()))
                .thenThrow(new IllegalArgumentException("O título da pauta não pode ser nulo ou vazio"));

        PautaController controller = new PautaController(pautaServiceMock, votoService);

        // ACT & ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> controller.criarPauta(request)
        );
        assertEquals("O título da pauta não pode ser nulo ou vazio", exception.getMessage());
    }

    @Test
    void criarPauta_deveRetornar409_quandoTituloDuplicado() {
        // ARRANGE
        PautaDTO request = new PautaDTO("Duplicada");
        when(pautaService.criarPauta(any(Pauta.class)))
                .thenThrow(new PautaDuplicadaException("Duplicada"));

        // ACT & ASSERT
        assertThrows(PautaDuplicadaException.class,
                () -> pautaController.criarPauta(request));
    }
    //endregion

    //region @Test - Abrir Sessao
    @Test
    void abrirSessao_deveRetornar200_quandoSucesso() {
        // ARRANGE
        String titulo = "Pauta Teste";
        TituloPauta tituloPauta = new TituloPauta(titulo);
        Pauta pauta = new Pauta(tituloPauta);

        when(pautaService.buscarPorTitulo(titulo)).thenReturn(Optional.of(pauta));
        when(pautaService.abrirSessao(eq(titulo), anyInt())).thenReturn(pauta);

        AbrirSessaoRequestDTO request = new AbrirSessaoRequestDTO(5);

        // ACT
        ResponseEntity<AbrirSessaoResponseDTO> response = pautaController.abrirSessao(titulo, request);

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
        assertThrows(PautaNaoEncontradaException.class,
                () -> pautaController.abrirSessao(titulo, request));
    }
    //endregion

    //region @Test - Votar
    @Test
    void votar_deveRetornar201_quandoSucesso() {
        // ARRANGE
        String tituloPauta = "Pauta Teste";
        String cpf = "12345678900";
        String escolha = "SIM";

        VotoRequestDTO request = new VotoRequestDTO(cpf, escolha);

        TituloPauta titulo = new TituloPauta(tituloPauta);
        Pauta pauta = new Pauta(titulo);
        
        Cpf cpfObj = new Cpf(cpf);
        Voto voto = new Voto(cpfObj, Escolha.SIM, pauta);

        when(votoService.registrarVotoPorTitulo(tituloPauta, cpf, escolha)).thenReturn(voto);

        // ACT
        ResponseEntity<VotoResponseDTO> response = pautaController.votar(tituloPauta, request);

        // ASSERT
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        verify(votoService, times(1)).registrarVotoPorTitulo(tituloPauta, cpf, escolha);
    }

    @Test
    void votar_deveRetornar400_quandoCpfComCaracterInvalido() {
        // ARRANGE
        String titulo = "Pauta Teste";
        String cpf = "12345abc900"; // caracteres inválidos
        String escolha = "SIM";
        VotoRequestDTO request = new VotoRequestDTO(cpf, escolha);

        when(votoService.registrarVotoPorTitulo(titulo, cpf, escolha))
                .thenThrow(new IllegalArgumentException("Request Body inválido ou malformado"));

        // ACT & ASSERT
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> pautaController.votar(titulo, request));

        assertEquals("Request Body inválido ou malformado", ex.getMessage());
    }

    @Test
    void votar_deveRetornar400_quandoSessaoNaoAberta() {
        // ARRANGE
        String titulo = "Pauta Teste";
        String cpf = "12345678900";
        String escolha = "SIM";
        VotoRequestDTO request = new VotoRequestDTO(cpf, escolha);

        when(votoService.registrarVotoPorTitulo(titulo, cpf, escolha))
                .thenThrow(new SessaoNaoAbertaException());

        // ACT & ASSERT
        SessaoNaoAbertaException ex = assertThrows(SessaoNaoAbertaException.class,
                () -> pautaController.votar(titulo, request));

        assertTrue(ex.getMessage().contains("não foi aberta"));
    }

    @Test
    void votar_deveRetornar400_quandoSessaoFechada() {
        // ARRANGE
        String titulo = "Pauta Teste";
        String cpf = "12345678900";
        String escolha = "SIM";
        VotoRequestDTO request = new VotoRequestDTO(cpf, escolha);

        when(votoService.registrarVotoPorTitulo(titulo, cpf, escolha))
                .thenThrow(new SessaoFechadaException());

        // ACT & ASSERT
        SessaoFechadaException ex = assertThrows(SessaoFechadaException.class,
                () -> pautaController.votar(titulo, request));

        assertTrue(ex.getMessage().contains("fechada"));
    }

    @Test
    void votar_deveRetornar400_quandoEscolhaInvalida() {
        // ARRANGE
        String titulo = "Pauta Teste";
        String cpf = "12345678900";
        String escolha = "TALVEZ"; // inválido
        VotoRequestDTO request = new VotoRequestDTO(cpf, escolha);

        when(votoService.registrarVotoPorTitulo(titulo, cpf, escolha))
                .thenThrow(new IllegalArgumentException("A escolha deve ser 'SIM' ou 'NAO'"));

        // ACT & ASSERT
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> pautaController.votar(titulo, request));

        assertEquals("A escolha deve ser 'SIM' ou 'NAO'", ex.getMessage());
    }

    @Test
    void votar_deveRetornar400_quandoEscolhaVazia() {
        // ARRANGE
        String titulo = "Pauta Teste";
        String cpf = "12345678900";
        String escolha = null; // obrigatório
        VotoRequestDTO request = new VotoRequestDTO(cpf, escolha);

        when(votoService.registrarVotoPorTitulo(titulo, cpf, escolha))
                .thenThrow(new IllegalArgumentException("A escolha do voto é obrigatória"));

        // ACT & ASSERT
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> pautaController.votar(titulo, request));

        assertEquals("A escolha do voto é obrigatória", ex.getMessage());
    }

    @Test
    void votar_deveRetornar401_quandoNaoAutorizado() {
        // ARRANGE
        String titulo = "Pauta Teste";
        String cpf = "00000000000";
        String escolha = "SIM";

        VotoRequestDTO request = new VotoRequestDTO(cpf, escolha);

        when(votoService.registrarVotoPorTitulo(titulo, cpf, escolha))
                .thenThrow(new AssociadoNaoAutorizadoException());

        // ACT & ASSERT
        assertThrows(AssociadoNaoAutorizadoException.class,
                () -> pautaController.votar(titulo, request));
    }

    @Test
    void votar_deveRetornar404_quandoPautaNaoEncontrada() {
        // ARRANGE
        String titulo = "NaoExiste";
        String cpf = "12345678900";
        String escolha = "SIM";

        VotoRequestDTO request = new VotoRequestDTO(cpf, escolha);

        when(votoService.registrarVotoPorTitulo(titulo, cpf, escolha))
                .thenThrow(new PautaNaoEncontradaException(titulo));

        // ACT & ASSERT
        assertThrows(PautaNaoEncontradaException.class,
                () -> pautaController.votar(titulo, request));
    }

    @Test
    void votar_deveRetornar409_quandoAssociadoJaVotou() {
        // ARRANGE
        String titulo = "Pauta Teste";
        String cpf = "12345678900";
        String escolha = "SIM";

        VotoRequestDTO request = new VotoRequestDTO(cpf, escolha);

        when(votoService.registrarVotoPorTitulo(titulo, cpf, escolha))
                .thenThrow(new VotoDuplicadoException(cpf));

        // ACT & ASSERT
        assertThrows(VotoDuplicadoException.class,
                () -> pautaController.votar(titulo, request));
    }
    //endregion

    //region @Test - Resultado
    @Test
    void resultado_deveRetornar200_quandoSucessoAPROVADA() {
        // ARRANGE
        String titulo = "Pauta Teste";
        ResultadoVotacaoResponseDTO resultadoEsperado = new ResultadoVotacaoResponseDTO(
                titulo,
                new ResultadoVotacaoResponseDTO.ResultadoDTO(100, 0, "APROVADA")
        );

        when(votoService.calcularResultadoPorTitulo(titulo)).thenReturn(resultadoEsperado);

        // ACT
        ResponseEntity<ResultadoVotacaoResponseDTO> response = pautaController.resultado(titulo);

        // ASSERT
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(resultadoEsperado, response.getBody());
        Assertions.assertNotNull(response.getBody());
        assertEquals("APROVADA", response.getBody().resultado().status());
        verify(votoService, times(1)).calcularResultadoPorTitulo(titulo);
    }

    @Test
    void resultado_deveRetornar200_quandoSucessoREPROVADA() {
        // ARRANGE
        String titulo = "Pauta Teste";
        ResultadoVotacaoResponseDTO resultadoEsperado = new ResultadoVotacaoResponseDTO(
                titulo,
                new ResultadoVotacaoResponseDTO.ResultadoDTO(0, 100, "REPROVADA")
        );

        when(votoService.calcularResultadoPorTitulo(titulo)).thenReturn(resultadoEsperado);

        // ACT
        ResponseEntity<ResultadoVotacaoResponseDTO> response = pautaController.resultado(titulo);

        // ASSERT
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(resultadoEsperado, response.getBody());
        Assertions.assertNotNull(response.getBody());
        assertEquals("REPROVADA", response.getBody().resultado().status());
        verify(votoService, times(1)).calcularResultadoPorTitulo(titulo);
    }

    @Test
    void resultado_deveRetornar200_quandoSucessoEMPATE() {
        // ARRANGE
        String titulo = "Pauta Teste";
        ResultadoVotacaoResponseDTO resultadoEsperado = new ResultadoVotacaoResponseDTO(
                titulo,
                new ResultadoVotacaoResponseDTO.ResultadoDTO(50, 50, "EMPATE")
        );

        when(votoService.calcularResultadoPorTitulo(titulo)).thenReturn(resultadoEsperado);

        // ACT
        ResponseEntity<ResultadoVotacaoResponseDTO> response = pautaController.resultado(titulo);

        // ASSERT
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(resultadoEsperado, response.getBody());
        Assertions.assertNotNull(response.getBody());
        assertEquals("EMPATE", response.getBody().resultado().status());
        verify(votoService, times(1)).calcularResultadoPorTitulo(titulo);
    }

    @Test
    void resultado_deveRetornar404_quandoPautaNaoEncontrada() {
        // ARRANGE
        String titulo = "NaoExiste";

        when(votoService.calcularResultadoPorTitulo(titulo))
                .thenThrow(new PautaNaoEncontradaException(titulo));

        // ACT & ASSERT
        assertThrows(PautaNaoEncontradaException.class,
                () -> pautaController.resultado(titulo));
    }
    //endregion
}
