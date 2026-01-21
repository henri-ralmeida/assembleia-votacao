package com.henrique.votacao.service;

import com.henrique.votacao.domain.model.pauta.Pauta;
import com.henrique.votacao.domain.model.pauta.TituloPauta;
import com.henrique.votacao.domain.exception.PautaNaoEncontradaException;
import com.henrique.votacao.repository.PautaRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;

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
        TituloPauta titulo = new TituloPauta("Nova Pauta");
        Pauta pauta = new Pauta(titulo);
        when(pautaRepository.save(any(Pauta.class))).thenReturn(pauta);
        when(pautaRepository.findByTituloPauta("Nova Pauta")).thenReturn(Optional.empty());

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
        TituloPauta tituloPauta = new TituloPauta(titulo);
        Pauta pauta = new Pauta(tituloPauta);
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
        PautaNaoEncontradaException ex = assertThrows(PautaNaoEncontradaException.class,
                () -> pautaService.abrirSessao(titulo, null));

        // ASSERT
        assertTrue(ex.getMessage().contains("Pauta não encontrada"));
    }
}
