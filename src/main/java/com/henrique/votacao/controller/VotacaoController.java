package com.henrique.votacao.controller;

import com.henrique.votacao.domain.*;
import com.henrique.votacao.dto.*;
import com.henrique.votacao.service.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Tag(name = "Votações", description = "Endpoints para gerenciamento de Pautas e Votações")
@RestController
@RequestMapping("/api/v1/votacoes")
public class VotacaoController {

    private static final Logger logger = LoggerFactory.getLogger(VotacaoController.class);

    private final PautaService pautaService;
    private final VotoService votoService;

    public VotacaoController(PautaService pautaService, VotoService votoService) {
        this.pautaService = pautaService;
        this.votoService = votoService;
    }

    @Operation(summary = "Cria uma nova pauta")
    @ApiResponse(responseCode = "201", description = "Pauta criada com sucesso",
            content = @Content(schema = @Schema(implementation = PautaRequestDTO.class)))
    @ApiResponse(responseCode = "400", description = "O título é obrigatório")
    @PostMapping
    public ResponseEntity<PautaRequestDTO> criarPauta(@RequestBody @Valid PautaRequestDTO request) {
        logger.info("Recebida requisição para criar pauta: {}", request.titulo());

        Pauta pauta = new Pauta();
        pauta.setTitulo(request.titulo());

        Pauta criada = pautaService.criarPauta(pauta);

        logger.info("Pauta criada com sucesso: ID={}, Titulo={}", criada.getId(), criada.getTitulo());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new PautaRequestDTO(criada.getTitulo()));
    }

    @Operation(summary = "Abre sessão de votação")
    @ApiResponse(responseCode = "200", description = "Sessão aberta com sucesso",
            content = @Content(schema = @Schema(implementation = PautaRequestDTO.class)))
    @ApiResponse(responseCode = "400", description = "O título da pauta é obrigatório")
    @ApiResponse(responseCode = "404", description = "Pauta não encontrada")
    @PostMapping("/abrir")
    public ResponseEntity<String> abrirSessao(@RequestBody @Valid AbrirSessaoRequestDTO request) {
        logger.info("Recebida requisição para abrir sessão: titulo={}, duracaoMinutos={}",
                request.titulo(), request.duracaoMinutos());

        Pauta pauta = pautaService.buscarPorTitulo(request.titulo())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pauta não encontrada"));

        pautaService.abrirSessao(pauta.getTitulo(), request.duracaoMinutos());

        logger.info("Sessão aberta com sucesso: pautaID={}, duracao={}min", pauta.getId(), request.duracaoMinutos());

        return ResponseEntity.ok("Sessão de votação aberta por "
                + ((request.duracaoMinutos() == null || request.duracaoMinutos() < 1) ? 1 : request.duracaoMinutos())
                + " minuto(s) para a pauta: " + pauta.getTitulo());

    }

    @Operation(summary = "Registra um voto")
    @ApiResponse(responseCode = "201", description = "Voto registrado com sucesso",
            content = @Content(schema = @Schema(implementation = VotoRequestDTO.class)))
    @ApiResponse(responseCode = "400", description = "O CPF deve conter exatamente 11 números")
    @ApiResponse(responseCode = "401", description = "Associado não autorizado a votar")
    @ApiResponse(responseCode = "404", description = "Pauta não encontrada")
    @ApiResponse(responseCode = "409", description = "Associado já votou")
    @PostMapping("/votos")
    public ResponseEntity<VotoRequestDTO> votar(@Valid @RequestBody VotoRequestDTO request) {
        logger.info("Recebida requisição de voto: titulo={}, associadoID={}, escolha={}",
                request.titulo(), request.associadoId(), request.escolha());

        Voto voto = votoService.registrarVotoPorTitulo(request.titulo(), request.associadoId(), request.escolha());

        logger.info("Voto registrado com sucesso: titulo={}, associadoID={}, escolha={}",
                request.titulo(), voto.getAssociadoId(), voto.getEscolha());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new VotoRequestDTO(request.titulo(), voto.getAssociadoId(), voto.getEscolha().name()));
    }

    @Operation(summary = "Obtém o resultado da votação")
    @ApiResponse(responseCode = "200", description = "Resultado retornado com sucesso",
            content = @Content(schema = @Schema(implementation = String.class, example = "SIM=7, NAO=3")))
    @ApiResponse(responseCode = "404", description = "Pauta não encontrada")
    @GetMapping("/resultado")
    public ResponseEntity<String> resultado(
            @RequestParam(value = "titulo") String titulo) {
        logger.info("Recebida requisição de resultado: titulo={}", titulo);

        String resultado = votoService.calcularResultadoPorTitulo(titulo);

        logger.info("Resultado retornado: titulo={}, resultado={}", titulo, resultado);

        return ResponseEntity.ok(resultado);
    }
}
