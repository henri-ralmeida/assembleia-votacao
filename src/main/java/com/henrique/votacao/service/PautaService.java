package com.henrique.votacao.service;

import com.henrique.votacao.domain.Pauta;
import com.henrique.votacao.exception.BusinessException;
import com.henrique.votacao.exception.NotFoundException;
import com.henrique.votacao.repository.PautaRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
     * @throws BusinessException caso já exista uma pauta com o mesmo título
     */
    public Pauta criarPauta(Pauta pauta) {
        logger.info("Criando nova pauta: {}", pauta.getTitulo());

        if (pautaRepository.existsByTitulo(pauta.getTitulo())) {
            throw new BusinessException("Já existe uma pauta com esse título");
        }

        return pautaRepository.save(pauta);
    }

    /**
     * Abre sessão de votação para uma pauta identificada pelo título.
     * A duração padrão é de 1 minuto caso não seja informada.
     * @param titulo título da pauta
     * @param duracaoMinutos duração da sessão em minutos (opcional)
     * @return Pauta com sessão aberta (com abertura e fechamento definidos)
     * @throws NotFoundException caso não exista pauta com o título informado
     */
    public Pauta abrirSessao(String titulo, Integer duracaoMinutos) {
        Pauta pauta = pautaRepository.findByTitulo(titulo)
                .orElseThrow(() -> {
                    logger.error("Pauta não encontrada: titulo={}", titulo);
                    return new NotFoundException("Pauta não encontrada");
                });

        if (duracaoMinutos == null || duracaoMinutos < 1) {
            duracaoMinutos = 1;
            logger.warn("Duração inválida fornecida. Forçando sessão para 1 minuto");
        }

        logger.info("Abrindo sessão para pauta '{}' com duração {} minuto(s)", titulo, duracaoMinutos);

        LocalDateTime agora = LocalDateTime.now();

        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH'h'mm'm'ss's'");

        pauta.setAbertura(agora.format(formatador));
        pauta.setFechamento(agora.plusMinutes(duracaoMinutos).format(formatador));

        return pautaRepository.save(pauta);
    }

    /**
     * Busca uma pauta pelo título.
     * @param titulo título da pauta
     * @return Optional com a pauta encontrada, ou vazio caso não exista
     */
    public Optional<Pauta> buscarPorTitulo(String titulo) {
        return pautaRepository.findByTitulo(titulo);
    }
}
