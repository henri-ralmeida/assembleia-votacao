package com.henrique.votacao.service;

import com.henrique.votacao.domain.Pauta;
import com.henrique.votacao.exception.BusinessException;
import com.henrique.votacao.exception.NotFoundException;
import com.henrique.votacao.repository.PautaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PautaService {

    private static final Logger logger = LoggerFactory.getLogger(PautaService.class);

    private final PautaRepository pautaRepository;

    public PautaService(PautaRepository pautaRepository) {
        this.pautaRepository = pautaRepository;
    }

    public Pauta criarPauta(Pauta pauta) {
        logger.info("Criando nova pauta: {}", pauta.getTitulo());

        if (pautaRepository.existsByTitulo(pauta.getTitulo())) {
            throw new BusinessException("Já existe uma pauta com esse título");
        }

        return pautaRepository.save(pauta);
    }

    public Pauta abrirSessao(Long pautaId, Integer duracaoMinutos) {
        Pauta pauta = buscarPautaOuFalhar(pautaId);
        int duracao = duracaoMinutos != null ? duracaoMinutos : 1;

        logger.info("Abrindo sessão para pautaID={} com duração {} minuto(s)", pautaId, duracao);

        LocalDateTime agora = LocalDateTime.now();
        pauta.setAbertura(agora);
        pauta.setFechamento(agora.plusMinutes(duracao));

        return pautaRepository.save(pauta);
    }

    public Pauta buscarPautaOuFalhar(Long pautaId) {
        return pautaRepository.findById(pautaId)
                .orElseThrow(() -> {
                    logger.error("Pauta não encontrada: ID={}", pautaId);
                    return new NotFoundException("Pauta não encontrada");
                });
    }
}
