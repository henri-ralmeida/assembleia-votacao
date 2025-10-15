package com.henrique.votacao.service;

import com.henrique.votacao.client.CpfClientFake;
import com.henrique.votacao.domain.*;
import com.henrique.votacao.exception.BusinessException;
import com.henrique.votacao.repository.VotoRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
public class VotoService {

    private static final Logger logger = LoggerFactory.getLogger(VotoService.class);

    private final PautaService pautaService;
    private final VotoRepository votoRepository;
    private final CpfClientFake cpfClient;

    private static final DateTimeFormatter FORMATADOR = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH'h'mm'm'ss's'");

    public VotoService(PautaService pautaService, VotoRepository votoRepository, CpfClientFake cpfClient) {
        this.pautaService = pautaService;
        this.votoRepository = votoRepository;
        this.cpfClient = cpfClient;
    }

    /**
     * Registra um voto para uma pauta pelo título
     * @param titulo Título da pauta
     * @param associadoId CPF ou ID do associado
     * @param escolhaStr "SIM" ou "NAO"
     * @return Voto registrado
     * @throws ResponseStatusException retorna um HTTP 404 Not Found quando nao encontra a Pauta
     */
    public Voto registrarVotoPorTitulo(String titulo, String associadoId, String escolhaStr) {
        Pauta pauta = pautaService.buscarPorTitulo(titulo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pauta não encontrada"));

        validarSessaoAberta(pauta);
        validarVotoUnico(associadoId, pauta.getId());
        validarCpf(associadoId);

        Escolha escolha = parseEscolha(escolhaStr);

        Voto voto = new Voto();
        voto.setAssociadoId(associadoId);
        voto.setEscolha(escolha);
        voto.setPauta(pauta);

        return votoRepository.save(voto);
    }

    /**
     * Calcula o resultado da pauta pelo título, incluindo porcentagem e aprovação/reprovação
     * @param titulo Título da pauta
     * @return Resultado da votação
     * @throws ResponseStatusException retorna um HTTP 404 Not Found quando nao encontra a Pauta
     */
    public String calcularResultadoPorTitulo(String titulo) {
        Pauta pauta = pautaService.buscarPorTitulo(titulo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pauta não encontrada"));

        long votosSim = votoRepository.countByPautaAndEscolha(pauta, Escolha.SIM);
        long votosNao = votoRepository.countByPautaAndEscolha(pauta, Escolha.NAO);

        long totalVotos = votosSim + votosNao;

        if (totalVotos == 0) {
            return "Nenhum voto registrado para a pauta '" + pauta.getTitulo() + "'";
        }

        double  percSim = (int) ((votosSim * 100) / totalVotos);
        double  percNao = (int) ((votosNao * 100) / totalVotos);

        String resultadoFinal = (votosSim > votosNao) ? "APROVADA" : "REPROVADA";

        return String.format(
                "%s: %.2f%% das pessoas votaram SIM, %.2f%% das pessoas votaram NAO, portanto a pauta está %s.",
                pauta.getTitulo(), percSim, percNao, resultadoFinal
        );
    }

    /**
     * Valida se a sessão da pauta está aberta usando a string formatada
     * @param pauta Pauta
     * @throws ResponseStatusException retorna um HTTP 404 Not Found quando nao encontra a Pauta
     */
    private void validarSessaoAberta(Pauta pauta) {
        if (pauta.getAbertura() == null || pauta.getFechamento() == null) {
            throw new BusinessException("Sessão de votação não foi aberta");
        }

        LocalDateTime abertura = LocalDateTime.parse(pauta.getAbertura(), FORMATADOR);
        LocalDateTime fechamento = LocalDateTime.parse(pauta.getFechamento(), FORMATADOR);
        LocalDateTime agora = LocalDateTime.now();

        if (agora.isBefore(abertura) || agora.isAfter(fechamento)) {
            throw new BusinessException("Sessão de votação fechada");
        }
    }

    /**
     * Verifica se o associado já votou na pauta
     * @param associadoId CPF do Associado
     * @param pautaId Id da Pauta
     * @throws ResponseStatusException retorna um HTTP 409 Conflict quando já uma pauta com o mesmo título
     */
    private void validarVotoUnico(String associadoId, Long pautaId) {
        if (votoRepository.existsByAssociadoIdAndPautaId(associadoId, pautaId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Associado já votou");
        }
    }

    /**
     * Valida o CPF do associado usando o Client Fake
     * @param associadoId CPF do associado
     * @throws ResponseStatusException retorna um HTTP 401 Unathorized quando o associado não está autorizado a votar
     */
    private void validarCpf(String associadoId) {
        Map<String, String> resposta = cpfClient.verificarCpf();
        String status = resposta.get("status");

        if (!"ABLE_TO_VOTE".equalsIgnoreCase(status)) {
            logger.warn("Associado não autorizado a votar: associadoID={}, status={}", associadoId, status);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Associado não autorizado a votar");
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
