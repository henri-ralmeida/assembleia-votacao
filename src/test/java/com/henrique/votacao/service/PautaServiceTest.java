package com.henrique.votacao.service;

import com.henrique.votacao.domain.Pauta;
import com.henrique.votacao.repository.PautaRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PautaServiceTest {

    private PautaRepository pautaRepository;
    private PautaService pautaService;

    @BeforeEach
    void setUp() {
        pautaRepository = Mockito.mock(PautaRepository.class);
        pautaService = new PautaService(pautaRepository);
    }

    @Test
    void criarPauta_deveSalvarPauta() {
        // ARRANGE
        Pauta pauta = new Pauta();
        pauta.setTituloPauta("Nova Pauta");
        when(pautaRepository.save(any(Pauta.class))).thenReturn(pauta);
        when(pautaRepository.existsByTituloPauta("Nova Pauta")).thenReturn(false);

        // ACT
        Pauta result = pautaService.criarPauta(pauta);

        // ASSERT
        assertNotNull(result);
        assertEquals("Nova Pauta", result.getTituloPauta());
        verify(pautaRepository, times(1)).save(pauta);
    }

    @Test
    void abrirSessao_deveAbrirSessaoComDuracaoDefault() {
        // ARRANGE
        String titulo = "Pauta Teste";
        Pauta pauta = new Pauta();
        pauta.setTituloPauta(titulo);
        when(pautaRepository.findByTituloPauta(titulo)).thenReturn(Optional.of(pauta));
        when(pautaRepository.save(any(Pauta.class))).thenReturn(pauta);

        // ACT
        Pauta result = pautaService.abrirSessao(titulo, null);

        // ASSERT
        assertNotNull(result.getAbertura());
        assertNotNull(result.getFechamento());
        assertFalse(result.getAbertura().isEmpty());
        assertFalse(result.getFechamento().isEmpty());
        verify(pautaRepository, times(1)).save(pauta);
    }

    @Test
    void abrirSessao_pautaNaoEncontrada_deveLancarExcecao() {
        // ARRANGE
        String titulo = "Pauta Inexistente";
        when(pautaRepository.findByTituloPauta(titulo)).thenReturn(Optional.empty());

        // ACT
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> pautaService.abrirSessao(titulo, null));

        // ASSERT
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertEquals("Pauta não encontrada", ex.getReason());
    }
}
