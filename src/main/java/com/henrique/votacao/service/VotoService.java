package com.henrique.votacao.service;

import com.henrique.votacao.client.CpfClientFake;
import com.henrique.votacao.domain.Escolha;
import com.henrique.votacao.domain.Pauta;
import com.henrique.votacao.domain.Voto;
import com.henrique.votacao.exception.BusinessException;
import com.henrique.votacao.repository.VotoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class VotoService {

    private static final Logger logger = LoggerFactory.getLogger(VotoService.class);

    private final PautaService pautaService;
    private final VotoRepository votoRepository;
    private final CpfClientFake cpfClient;

    public VotoService(PautaService pautaService, VotoRepository votoRepository, CpfClientFake cpfClient) {
        this.pautaService = pautaService;
        this.votoRepository = votoRepository;
        this.cpfClient = cpfClient;
    }

    public Voto registrarVoto(Long pautaId, String associadoId, String escolhaStr) {
        Pauta pauta = pautaService.buscarPautaOuFalhar(pautaId);

        LocalDateTime agora = LocalDateTime.now();
        if (pauta.getAbertura() != null && pauta.getFechamento() != null) {
            if (agora.isBefore(pauta.getAbertura()) || agora.isAfter(pauta.getFechamento())) {
                throw new BusinessException("Sessão de votação fechada");
            }
        }

        if (votoRepository.existsByAssociadoIdAndPautaId(associadoId, pautaId)) {
            throw new BusinessException("Associado já votou");
        }

        Map<String, String> resposta = cpfClient.verificarCpf(associadoId);
        String status = resposta.get("status");

        if ("INVALID".equalsIgnoreCase(status)) {
            throw new BusinessException("CPF inválido");
        }

        if (!"ABLE_TO_VOTE".equalsIgnoreCase(status)) {
            logger.warn("Associado não autorizado a votar: associadoID={}, status={}", associadoId, status);
            throw new BusinessException("Associado não autorizado a votar");
        }

        Escolha escolha;
        try {
            escolha = Escolha.valueOf(escolhaStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Escolha inválida. Use SIM ou NAO.");
        }

        Voto voto = new Voto();
        voto.setAssociadoId(associadoId);
        voto.setEscolha(escolha);
        voto.setPauta(pauta);

        return votoRepository.save(voto);
    }

    public String calcularResultado(Long pautaId) {
        Pauta pauta = pautaService.buscarPautaOuFalhar(pautaId);
        long votosSim = votoRepository.countByPautaAndEscolha(pauta, Escolha.SIM);
        long votosNao = votoRepository.countByPautaAndEscolha(pauta, Escolha.NAO);
        return "Resultado da pauta " + pautaId + ": SIM=" + votosSim + ", NAO=" + votosNao;
    }
}
