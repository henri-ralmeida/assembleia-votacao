package com.henrique.votacao.controller;

import com.henrique.votacao.domain.Pauta;
import com.henrique.votacao.domain.Voto;
import com.henrique.votacao.dto.*;
import com.henrique.votacao.service.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Tag(name = "Assembleia", description = "Endpoints para gerenciamento de Pautas e Votações em uma Assembleia")
@RestController
@RequestMapping("/api/v1/pautas")
public class PautaController {

    private static final Logger logger = LoggerFactory.getLogger(PautaController.class);

    private final PautaService pautaService;
    private final VotoService votoService;

    public PautaController(PautaService pautaService, VotoService votoService) {
        this.pautaService = pautaService;
        this.votoService = votoService;
    }

    @Operation(summary = "Cria uma nova pauta")
    @ApiResponse(responseCode = "201", description = "Pauta criada com sucesso",
            content = @Content(schema = @Schema(implementation = PautaDTO.class))
    )
    @ApiResponse(responseCode = "400", description = "O título da pauta é obrigatório")
    @ApiResponse(responseCode = "409", description = "Já existe uma pauta com esse título")
    @PostMapping
    public ResponseEntity<PautaDTO> criarPauta(@RequestBody @Valid PautaDTO request) {
        logger.info("Recebida requisição para criar pauta: {}", request.tituloPauta());

        Pauta pauta = new Pauta();
        pauta.setTituloPauta(request.tituloPauta());

        Pauta criada = pautaService.criarPauta(pauta);

        logger.info("Pauta criada com sucesso: ID={}, Titulo={}", criada.getId(), criada.getTituloPauta());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new PautaDTO(criada.getTituloPauta()));
    }

    @Operation(summary = "Abre uma sessão de votação em uma pauta")
    @ApiResponse(responseCode = "200", description = "Sessão aberta com sucesso",
            content = @Content(schema = @Schema(implementation = AbrirSessaoResponseDTO.class))
    )
    @ApiResponse(responseCode = "404", description = "Pauta não encontrada")
    @PostMapping("/{tituloPauta}/sessoes")
    public ResponseEntity<AbrirSessaoResponseDTO> abrirSessao(
            @PathVariable String tituloPauta,
            @RequestBody @Valid AbrirSessaoRequestDTO request) {

        logger.info("Requisição para abrir sessão: titulo={}, duracaoMinutos={}", tituloPauta, request.duracaoMinutos());

        Pauta pauta = pautaService.buscarPorTitulo(tituloPauta)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pauta não encontrada"));

        pautaService.abrirSessao(pauta.getTituloPauta(), request.duracaoMinutos());

        logger.info("Sessão aberta com sucesso: pautaID={}, duracao={}min", pauta.getId(), request.duracaoMinutos());

        AbrirSessaoResponseDTO response = AbrirSessaoResponseDTO.criarMensagem(pauta.getTituloPauta(), pauta.getDuracaoMinutos());

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Registra um voto em uma pauta")
    @ApiResponse(responseCode = "201", description = "Voto registrado com sucesso",
            content = @Content(schema = @Schema(implementation = VotoResponseDTO.class)))
    @ApiResponse(responseCode = "400", description = "O CPF deve conter exatamente 11 números")
    @ApiResponse(responseCode = "401", description = "Associado não autorizado a votar")
    @ApiResponse(responseCode = "404", description = "Pauta não encontrada")
    @ApiResponse(responseCode = "409", description = "Associado já votou")
    @PostMapping("/{tituloPauta}/votos")
    public ResponseEntity<VotoResponseDTO> votar(
            @PathVariable String tituloPauta,
            @Valid @RequestBody VotoRequestDTO request) {

        logger.info("Recebida requisição de voto: titulo={}, cpf={}, escolha={}",
                tituloPauta, request.cpf(), request.escolha());

        Voto voto = votoService.registrarVotoPorTitulo(tituloPauta, request.cpf(), request.escolha());

        logger.info("Voto registrado com sucesso: titulo={}, cpf={}, escolha={}",
                voto.getPauta().getTituloPauta(), voto.getCpfId(), voto.getEscolha());

        VotoResponseDTO response = VotoResponseDTO.criarMensagem(voto.getPauta().getTituloPauta(), voto.getCpfId(), voto.getEscolha().name());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @Operation(summary = "Obtém o resultado da votação de uma pauta")
    @ApiResponse(responseCode = "200", description = "Resultado retornado com sucesso",
            content = @Content(schema = @Schema(implementation = ResultadoVotacaoResponseDTO.class))
    )
    @ApiResponse(responseCode = "404", description = "Pauta não encontrada")
    @GetMapping("/{tituloPauta}/resultados")
    public ResponseEntity<ResultadoVotacaoResponseDTO> resultado(@PathVariable String tituloPauta) {
        logger.info("Recebida requisição de resultado: titulo={}", tituloPauta);

        ResultadoVotacaoResponseDTO resultadoVotacao = votoService.calcularResultadoPorTitulo(tituloPauta);

        logger.info("Resultado retornado: titulo={}, resultado={}", tituloPauta, resultadoVotacao);

        return ResponseEntity.ok(resultadoVotacao);
    }
}
