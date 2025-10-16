package com.henrique.votacao.controller;

import com.henrique.votacao.domain.Pauta;
import com.henrique.votacao.domain.Voto;
import com.henrique.votacao.dto.AbrirSessaoRequestDTO;
import com.henrique.votacao.dto.PautaRequestDTO;
import com.henrique.votacao.dto.VotoRequestDTO;
import com.henrique.votacao.service.PautaService;
import com.henrique.votacao.service.VotoService;

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

@Tag(name = "Pauta", description = "Endpoints para gerenciamento de Pautas e Votações em uma Assembleia")
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
            content = @Content(schema = @Schema(implementation = PautaRequestDTO.class)))
    @ApiResponse(responseCode = "400", description = "O título da pauta é obrigatório")
    @ApiResponse(responseCode = "409", description = "Já existe uma pauta com esse título")
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

    @Operation(summary = "Abre uma sessão de votação em uma pauta")
    @ApiResponse(responseCode = "200", description = "Sessão aberta com sucesso")
    @ApiResponse(responseCode = "404", description = "Pauta não encontrada")
    @PostMapping("/{titulo}/sessoes")
    public ResponseEntity<String> abrirSessao(
            @PathVariable String titulo,
            @RequestBody(required = false) @Valid AbrirSessaoRequestDTO request) {

        Integer duracao = (request == null || request.duracaoMinutos() == null || request.duracaoMinutos() < 1)
                ? 1
                : request.duracaoMinutos();

        logger.info("Requisição para abrir sessão: titulo={}, duracaoMinutos={}", titulo, duracao);

        Pauta pauta = pautaService.buscarPorTitulo(titulo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pauta não encontrada"));

        pautaService.abrirSessao(pauta.getTitulo(), duracao);

        logger.info("Sessão aberta com sucesso: pautaID={}, duracao={}min", pauta.getId(), duracao);

        return ResponseEntity.ok("Sessão de votação aberta por " + duracao + " minuto(s) para a pauta: " + titulo);
    }

    @Operation(summary = "Registra um voto em uma pauta")
    @ApiResponse(responseCode = "201", description = "Voto registrado com sucesso",
            content = @Content(schema = @Schema(implementation = VotoRequestDTO.class)))
    @ApiResponse(responseCode = "400", description = "O CPF deve conter exatamente 11 números")
    @ApiResponse(responseCode = "401", description = "Associado não autorizado a votar")
    @ApiResponse(responseCode = "404", description = "Pauta não encontrada")
    @ApiResponse(responseCode = "409", description = "Associado já votou")
    @PostMapping("/{titulo}/votos")
    public ResponseEntity<VotoRequestDTO> votar(
            @PathVariable String titulo,
            @Valid @RequestBody VotoRequestDTO request) {

        logger.info("Recebida requisição de voto: titulo={}, associadoID={}, escolha={}",
                titulo, request.associadoId(), request.escolha());

        Voto voto = votoService.registrarVotoPorTitulo(titulo, request.associadoId(), request.escolha());

        logger.info("Voto registrado com sucesso: titulo={}, associadoID={}, escolha={}",
                titulo, voto.getAssociadoId(), voto.getEscolha());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new VotoRequestDTO(voto.getAssociadoId(), voto.getEscolha().name()));
    }

    @Operation(summary = "Obtém o resultado da votação de uma pauta")
    @ApiResponse(responseCode = "200", description = "Resultado retornado com sucesso",
            content = @Content(schema = @Schema(implementation = String.class,
                     example = "X% das pessoas votaram SIM, Y% das pessoas votaram NAO, " +
                             "portanto a pauta está APROVADA|REPROVADA.")))
    @ApiResponse(responseCode = "404", description = "Pauta não encontrada")
    @GetMapping("/{titulo}/resultados")
    public ResponseEntity<String> resultado(@PathVariable String titulo) {
        logger.info("Recebida requisição de resultado: titulo={}", titulo);

        String resultado = votoService.calcularResultadoPorTitulo(titulo);

        logger.info("Resultado retornado: titulo={}, resultado={}", titulo, resultado);

        return ResponseEntity.ok(resultado);
    }
}
