package com.henrique.votacao.service;

import com.henrique.votacao.infrastructure.client.CpfClientFake;
import com.henrique.votacao.domain.exception.*;
import com.henrique.votacao.domain.model.associado.StatusVotacao;
import com.henrique.votacao.domain.model.pauta.Pauta;
import com.henrique.votacao.domain.model.voto.Cpf;
import com.henrique.votacao.domain.model.voto.Escolha;
import com.henrique.votacao.domain.model.voto.Voto;
import com.henrique.votacao.application.dto.response.ResultadoVotacaoResponseDTO;
import com.henrique.votacao.exception.BusinessException;
import com.henrique.votacao.repository.VotoRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

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

    /**
     * Registra um voto para uma pauta pelo título
     * @param tituloPauta Título da pauta
     * @param cpfNumero CPF ou ID do associado
     * @param escolhaStr "SIM" ou "NAO"
     * @return Voto registrado
     * @throws PautaNaoEncontradaException quando a pauta não é encontrada
     */
    public Voto registrarVotoPorTitulo(String tituloPauta, String cpfNumero, String escolhaStr) {
        Pauta pauta = pautaService.buscarPorTitulo(tituloPauta)
                .orElseThrow(() -> new PautaNaoEncontradaException(tituloPauta));

        validarSessaoAberta(pauta);
        validarVotoUnico(cpfNumero, pauta.getId());
        validarCpf(cpfNumero);

        Cpf cpf = new Cpf(cpfNumero);
        Escolha escolha = parseEscolha(escolhaStr);

        Voto voto = new Voto(cpf, escolha, pauta);

        return votoRepository.save(voto);
    }

    /**
     * Calcula o resultado da pauta pelo título, incluindo porcentagem e aprovação/reprovação
     * @param tituloPauta Título da pauta
     * @return Resultado da votação
     * @throws PautaNaoEncontradaException quando a pauta não é encontrada
     */
    public ResultadoVotacaoResponseDTO calcularResultadoPorTitulo(String tituloPauta) {
        Pauta pauta = pautaService.buscarPorTitulo(tituloPauta)
                .orElseThrow(() -> new PautaNaoEncontradaException(tituloPauta));

        long votosSim = votoRepository.countByPautaAndEscolha(pauta, Escolha.SIM);
        long votosNao = votoRepository.countByPautaAndEscolha(pauta, Escolha.NAO);

        long totalVotos = votosSim + votosNao;

        if (totalVotos == 0) {
            return new ResultadoVotacaoResponseDTO("Nenhum voto registrado para a pauta '" + pauta.getTituloPauta() + "'"
                    , new ResultadoVotacaoResponseDTO.ResultadoDTO(0,0,"SEM_VOTOS"));
        }

        double percSim = (int) ((votosSim * 100) / totalVotos);
        double percNao = (int) ((votosNao * 100) / totalVotos);

        String statusFinal;

        if (votosSim > votosNao) {
            statusFinal = "APROVADA";
        } else if (votosNao > votosSim) {
            statusFinal = "REPROVADA";
        } else {
            statusFinal = "EMPATE";
        }

        return new ResultadoVotacaoResponseDTO(
                pauta.getTituloPauta(),
                new ResultadoVotacaoResponseDTO.ResultadoDTO(percSim, percNao, statusFinal)
        );
    }

    /**
     * Valida se a sessão da pauta está aberta
     * @param pauta Pauta
     * @throws SessaoNaoAbertaException se a sessão não foi aberta
     * @throws SessaoFechadaException se a sessão já foi fechada
     */
    private void validarSessaoAberta(Pauta pauta) {
        if (!pauta.temSessaoAberta()) {
            throw new SessaoNaoAbertaException();
        }

        if (!pauta.podeReceberVoto()) {
            throw new SessaoFechadaException();
        }
    }

    /**
     * Verifica se o associado já votou na pauta
     * @param cpf CPF do Associado
     * @param pautaId Id da Pauta
     * @throws VotoDuplicadoException se o associado já votou
     */
    private void validarVotoUnico(String cpf, Long pautaId) {
        if (votoRepository.existsBycpfIdAndPautaId(cpf, pautaId)) {
            throw new VotoDuplicadoException(cpf);
        }
    }

    /**
     * Valida o CPF do associado usando o Client Fake
     * @param cpf CPF do associado
     * @throws AssociadoNaoAutorizadoException quando o associado não está autorizado a votar
     */
    private void validarCpf(String cpf) {
        Map<String, String> resposta = cpfClient.verificarCpf();
        String status = resposta.get("status");

        if (!"ABLE_TO_VOTE".equalsIgnoreCase(status)) {
            logger.warn("Associado não autorizado a votar: cpf={}, status={}", cpf, status);
            throw new AssociadoNaoAutorizadoException(cpf);
        }
    }

    /**
     * Converte String para Enum Escolha
     * @param escolhaStr String para Enum
     * @return Resultado da Escolha em Enum
     * @throws BusinessException retorna uma mensagem dizendo que a escolha está inválida
     */
    private Escolha parseEscolha(String escolhaStr) {
        try {
            return Escolha.valueOf(escolhaStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Escolha inválida. Use SIM ou NAO.");
        }
    }
}
