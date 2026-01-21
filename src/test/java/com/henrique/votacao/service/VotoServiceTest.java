package com.henrique.votacao.service;

import com.henrique.votacao.infrastructure.client.CpfClientFake;
import com.henrique.votacao.domain.model.pauta.Pauta;
import com.henrique.votacao.domain.model.pauta.TituloPauta;
import com.henrique.votacao.domain.model.voto.Voto;
import com.henrique.votacao.domain.model.voto.Escolha;
import com.henrique.votacao.domain.model.voto.Cpf;
import com.henrique.votacao.domain.exception.*;
import com.henrique.votacao.application.dto.response.ResultadoVotacaoResponseDTO;
import com.henrique.votacao.exception.*;
import com.henrique.votacao.repository.VotoRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class VotoServiceTest {

    private VotoRepository votoRepository;
    private PautaService pautaService;
    private CpfClientFake cpfClient;
    private VotoService votoService;

    @BeforeEach
    void setUp() {
        votoRepository = Mockito.mock(VotoRepository.class);
        pautaService = Mockito.mock(PautaService.class);
        cpfClient = Mockito.mock(CpfClientFake.class);
        votoService = new VotoService(pautaService, votoRepository, cpfClient);
    }

    @Test
    void registrarVoto_deveSalvarVoto() {
        // ARRANGE
        TituloPauta titulo = new TituloPauta("Pauta Teste");
        Pauta pauta = new Pauta(titulo);
        pauta.abrirSessao(5); // Opens session for 5 minutes

        when(pautaService.buscarPorTitulo("Pauta Teste")).thenReturn(Optional.of(pauta));
        when(votoRepository.existsBycpfIdAndPautaId("12345678901", pauta.getId())).thenReturn(false);
        when(cpfClient.verificarCpf()).thenReturn(Map.of("status", "ABLE_TO_VOTE"));
        when(votoRepository.save(any(Voto.class))).thenAnswer(i -> i.getArguments()[0]);

        // ACT
        Voto voto = votoService.registrarVotoPorTitulo("Pauta Teste", "12345678901", "SIM");

        // ASSERT
        assertNotNull(voto);
        assertEquals("12345678901", voto.getCpfId());
        assertEquals(Escolha.SIM, voto.getEscolha());
        assertNotNull(voto.getPauta());
        verify(votoRepository, times(1)).save(any(Voto.class));
    }

    @Test
    void registrarVoto_sessaoFechada_deveLancarExcecao() {
        // ARRANGE
        TituloPauta titulo = new TituloPauta("Pauta Teste");
        Pauta pauta = new Pauta(titulo);
        // Não abre a sessão ou cria uma sessão que já fechou (manualmente)
        // Simulamos uma pauta sem sessão aberta

        when(pautaService.buscarPorTitulo("Pauta Teste")).thenReturn(Optional.of(pauta));
        when(cpfClient.verificarCpf()).thenReturn(Map.of("status", "ABLE_TO_VOTE"));

        // ACT & ASSERT - Como a sessão não foi aberta, deve lançar SessaoNaoAbertaException
        SessaoNaoAbertaException ex = assertThrows(SessaoNaoAbertaException.class,
                () -> votoService.registrarVotoPorTitulo("Pauta Teste", "12345678901", "SIM"));

        assertTrue(ex.getMessage().contains("não foi aberta"));
        verify(votoRepository, never()).save(any(Voto.class));
    }

    @Test
    void registrarVoto_jaVotou_deveLancarExcecao() {
        // ARRANGE
        TituloPauta titulo = new TituloPauta("Pauta Teste");
        Pauta pauta = new Pauta(titulo);
        pauta.abrirSessao(5);

        when(pautaService.buscarPorTitulo("Pauta Teste")).thenReturn(Optional.of(pauta));
        when(votoRepository.existsBycpfIdAndPautaId("12345678901", pauta.getId())).thenReturn(true);
        when(cpfClient.verificarCpf()).thenReturn(Map.of("status", "ABLE_TO_VOTE"));

        // ACT & ASSERT
        VotoDuplicadoException ex = assertThrows(VotoDuplicadoException.class,
                () -> votoService.registrarVotoPorTitulo("Pauta Teste", "12345678901", "SIM"));

        assertTrue(ex.getMessage().contains("já votou"));
        verify(votoRepository, never()).save(any(Voto.class));
    }

    @Test
    void calcularResultado_deveContabilizarCorretamente() {
        // ARRANGE
        TituloPauta titulo = new TituloPauta("Pauta Teste");
        Pauta pauta = new Pauta(titulo);

        when(pautaService.buscarPorTitulo("Pauta Teste")).thenReturn(Optional.of(pauta));
        when(votoRepository.countByPautaAndEscolha(pauta, Escolha.SIM)).thenReturn(4L);
        when(votoRepository.countByPautaAndEscolha(pauta, Escolha.NAO)).thenReturn(2L);

        // ACT
        ResultadoVotacaoResponseDTO response = votoService.calcularResultadoPorTitulo("Pauta Teste");

        // ASSERT
        assertEquals("Pauta Teste", response.tituloPauta());
        assertEquals(66, response.resultado().sim());
        assertEquals(33, response.resultado().nao());
        assertEquals("APROVADA", response.resultado().status());
    }
}
