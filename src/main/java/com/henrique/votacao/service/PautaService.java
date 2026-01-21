package com.henrique.votacao.service;

import com.henrique.votacao.domain.exception.PautaDuplicadaException;
import com.henrique.votacao.domain.exception.PautaNaoEncontradaException;
import com.henrique.votacao.domain.model.pauta.Pauta;
import com.henrique.votacao.domain.model.pauta.TituloPauta;
import com.henrique.votacao.repository.PautaRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PautaService {

    private static final Logger logger = LoggerFactory.getLogger(PautaService.class);

    private final PautaRepository pautaRepository;

    public PautaService(PautaRepository pautaRepository) {
        this.pautaRepository = pautaRepository;
    }

    /**
     * Cria uma nova pauta no sistema.
     * @param pauta Objeto Pauta com os dados a serem salvos
     * @return Pauta criada com ID incremental
     * @throws PautaDuplicadaException quando já existe uma pauta com o mesmo título
     */
    public Pauta criarPauta(Pauta pauta) {
        logger.info("Criando nova pauta: {}", pauta.getTituloPauta());

        if (pautaRepository.existsByTituloPauta(pauta.getTituloPauta())) {
            throw new PautaDuplicadaException(pauta.getTituloPauta());
        }

        return pautaRepository.save(pauta);
    }

    /**
     * Abre sessão de votação para uma pauta identificada pelo título.
     * A duração padrão é de 1 minuto caso não seja informada.
     * @param titulo título da pauta
     * @param duracaoMinutos duração da sessão em minutos (opcional)
     * @return Pauta com sessão aberta (com abertura e fechamento definidos)
     * @throws PautaNaoEncontradaException quando a pauta não é encontrada
     */
    public Pauta abrirSessao(String titulo, Integer duracaoMinutos) {
        Pauta pauta = pautaRepository.findByTituloPauta(titulo)
                .orElseThrow(() -> {
                    logger.error("Pauta não encontrada: titulo={}", titulo);
                    return new PautaNaoEncontradaException(titulo);
                });

        logger.info("Abrindo sessão para pauta '{}' com duração {} minuto(s)", titulo, duracaoMinutos);

        pauta.abrirSessao(duracaoMinutos);

        return pautaRepository.save(pauta);
    }

    /**
     * Busca uma pauta pelo título.
     * @param titulo título da pauta
     * @return Optional com a pauta encontrada, ou vazio caso não exista
     */
    public Optional<Pauta> buscarPorTitulo(String titulo) {
        return pautaRepository.findByTituloPauta(titulo);
    }
}
