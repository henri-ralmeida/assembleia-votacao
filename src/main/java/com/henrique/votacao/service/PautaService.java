package com.henrique.votacao.service;

import com.henrique.votacao.domain.Escolha;
import com.henrique.votacao.domain.Pauta;
import com.henrique.votacao.domain.Voto;
import com.henrique.votacao.exception.BusinessException;
import com.henrique.votacao.exception.NotFoundException;
import com.henrique.votacao.repository.PautaRepository;
import com.henrique.votacao.repository.VotoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PautaService {

    private static final Logger logger = LoggerFactory.getLogger(PautaService.class);

    private final PautaRepository pautaRepository;
    private final VotoRepository votoRepository;

    public PautaService(PautaRepository pautaRepository, VotoRepository votoRepository) {
        this.pautaRepository = pautaRepository;
        this.votoRepository = votoRepository;
    }

    // Criar nova pauta
    public Pauta criarPauta(Pauta pauta) {
        logger.info("Criando nova pauta: {}", pauta.getTitulo());
        pauta.setAbertura(null); // ainda não aberta
        pauta.setFechamento(null);
        return pautaRepository.save(pauta);
    }

    // Abrir sessão de votação (tempo em minutos, default 1)
    public Pauta abrirSessao(Long pautaId, Integer duracaoMinutos) {
        logger.info("Abrindo sessão de votação para pauta ID={} com duração {} minuto(s)", pautaId, duracaoMinutos != null ? duracaoMinutos : 1);
        Pauta pauta = pautaRepository.findById(pautaId)
                .orElseThrow(() -> new RuntimeException("Pauta não encontrada"));

        pauta.setAbertura(LocalDateTime.now());
        pauta.setFechamento(LocalDateTime.now().plusMinutes(duracaoMinutos != null ? duracaoMinutos : 1));

        return pautaRepository.save(pauta);
    }

    // Registrar voto
    public Voto votar(Long pautaId, String associadoId, String escolha) {
        logger.info("Registrando voto: pautaID={}, associadoID={}, escolha={}", pautaId, associadoId, escolha);

        Pauta pauta = pautaRepository.findById(pautaId)
                .orElseThrow(() -> {
                    logger.error("Pauta não encontrada: ID={}", pautaId);
                    return new NotFoundException("Pauta não encontrada");
                });

        // Verifica se a sessão está aberta
        LocalDateTime agora = LocalDateTime.now();
        if (pauta.getAbertura() == null || pauta.getFechamento() == null || agora.isAfter(pauta.getFechamento())) {
            logger.warn("Tentativa de votar fora do período da sessão: pautaID={}, associadoID={}", pautaId, associadoId);
            throw new BusinessException("Sessão de votação fechada");
        }

        // Verifica se o associado já votou
        if (votoRepository.existsByAssociadoIdAndPautaId(associadoId, pautaId)) {
            logger.warn("Associado já votou: pautaID={}, associadoID={}", pautaId, associadoId);
            throw new BusinessException("Associado já votou");
        }

        Voto voto = new Voto();
        voto.setAssociadoId(associadoId);
        voto.setEscolha(Escolha.valueOf(escolha.toUpperCase()));
        voto.setPauta(pauta);

        Voto salvo = votoRepository.save(voto);
        logger.info("Voto registrado com sucesso: {}", salvo);
        return salvo;
    }

    // Contabilizar Votos
    public String resultadoVotacao(Long pautaId) {
        logger.info("Consultando resultado da votação: pautaID={}", pautaId);

        Pauta pauta = pautaRepository.findById(pautaId)
                .orElseThrow(() -> {
                    logger.error("Pauta não encontrada ao consultar resultado: ID={}", pautaId);
                    return new NotFoundException("Pauta não encontrada");
                });

        List<Voto> votos = pauta.getVotos();
        long sim = votos.stream().filter(v -> v.getEscolha() == Escolha.SIM).count();
        long nao = votos.stream().filter(v -> v.getEscolha() == Escolha.NAO).count();


        String resultado = String.format("Resultado da Pauta %s: SIM=%d, NAO=%d", pauta.getTitulo(), sim, nao);
        logger.info("Resultado da votação calculado: {}", resultado);
        return resultado;
    }
}
