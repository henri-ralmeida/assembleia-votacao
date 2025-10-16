package com.henrique.votacao.service;

import com.henrique.votacao.client.CpfClientFake;
import com.henrique.votacao.domain.*;
import com.henrique.votacao.dto.ResultadoVotacaoResponseDTO;
import com.henrique.votacao.exception.*;
import com.henrique.votacao.repository.VotoRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;

import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    private static final DateTimeFormatter FORMATADOR = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH'h'mm'm'ss's'");

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
        Pauta pauta = new Pauta();
        pauta.setId(1L);
        pauta.setTituloPauta("Pauta Teste");
        pauta.setAbertura(LocalDateTime.now().minusMinutes(1).format(FORMATADOR));
        pauta.setFechamento(LocalDateTime.now().plusMinutes(5).format(FORMATADOR));

        when(pautaService.buscarPorTitulo("Pauta Teste")).thenReturn(Optional.of(pauta));
        when(votoRepository.existsBycpfIdAndPautaId("123", pauta.getId())).thenReturn(false);
        when(cpfClient.verificarCpf()).thenReturn(Map.of("status", "ABLE_TO_VOTE"));
        when(votoRepository.save(any(Voto.class))).thenAnswer(i -> i.getArguments()[0]);

        // ACT
        Voto voto = votoService.registrarVotoPorTitulo("Pauta Teste", "123", "SIM");

        // ASSERT
        assertNotNull(voto);
        assertEquals("123", voto.getCpfId());
        assertEquals(Escolha.SIM, voto.getEscolha());
        assertNotNull(voto.getPauta());
        assertEquals(pauta.getId(), voto.getPauta().getId());
        verify(votoRepository, times(1)).save(any(Voto.class));
    }

    @Test
    void registrarVoto_sessaoFechada_deveLancarExcecao() {
        // ARRANGE
        Pauta pauta = new Pauta();
        pauta.setId(1L);
        pauta.setTituloPauta("Pauta Teste");
        pauta.setAbertura(LocalDateTime.now().minusMinutes(5).format(FORMATADOR));
        pauta.setFechamento(LocalDateTime.now().minusMinutes(1).format(FORMATADOR));

        when(pautaService.buscarPorTitulo("Pauta Teste")).thenReturn(Optional.of(pauta));
        when(cpfClient.verificarCpf()).thenReturn(Map.of("status", "ABLE_TO_VOTE"));

        // ACT
        BusinessException ex = assertThrows(BusinessException.class,
                () -> votoService.registrarVotoPorTitulo("Pauta Teste", "123", "SIM"));

        // ASSERT
        assertEquals("Sessão de votação fechada", ex.getMessage());
        verify(votoRepository, never()).save(any(Voto.class));
    }

    @Test
    void registrarVoto_jaVotou_deveLancarExcecao() {
        // ARRANGE
        Pauta pauta = new Pauta();
        pauta.setId(1L);
        pauta.setTituloPauta("Pauta Teste");
        pauta.setAbertura(LocalDateTime.now().minusMinutes(1).format(FORMATADOR));
        pauta.setFechamento(LocalDateTime.now().plusMinutes(5).format(FORMATADOR));

        when(pautaService.buscarPorTitulo("Pauta Teste")).thenReturn(Optional.of(pauta));
        when(votoRepository.existsBycpfIdAndPautaId("123", pauta.getId())).thenReturn(true);
        when(cpfClient.verificarCpf()).thenReturn(Map.of("status", "ABLE_TO_VOTE"));

        // ACT
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Associado já votou");
                });

        // ASSERT
        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        assertEquals("Associado já votou", ex.getReason());

        verify(votoRepository, never()).save(any(Voto.class));
    }

    @Test
    void calcularResultado_deveContabilizarCorretamente() {
        // ARRANGE
        Pauta pauta = new Pauta();
        pauta.setId(1L);
        pauta.setTituloPauta("Pauta Teste");

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
