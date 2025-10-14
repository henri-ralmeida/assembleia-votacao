package com.henrique.votacao.controller;

import com.henrique.votacao.domain.Pauta;
import com.henrique.votacao.domain.Voto;
import com.henrique.votacao.dto.PautaDTO;
import com.henrique.votacao.dto.VotoDTO;
import com.henrique.votacao.dto.VotacaoRequestDTO;
import com.henrique.votacao.service.PautaService;
import com.henrique.votacao.service.VotoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @ApiResponse(responseCode = "200", description = "Pauta criada com sucesso",
            content = @Content(schema = @Schema(implementation = PautaDTO.class)))
    @PostMapping
    public ResponseEntity<PautaDTO> criarPauta(@RequestBody Pauta pauta) {
        logger.info("Recebida requisição para criar pauta: {}", pauta.getTitulo());
        Pauta criada = pautaService.criarPauta(pauta);
        logger.info("Pauta criada com sucesso: ID={}, Titulo={}", criada.getId(), criada.getTitulo());
        return ResponseEntity.ok(new PautaDTO(criada.getId(), criada.getTitulo()));
    }

    @Operation(summary = "Abre sessão de votação")
    @ApiResponse(responseCode = "200", description = "Sessão aberta com sucesso",
            content = @Content(schema = @Schema(implementation = PautaDTO.class)))
    @ApiResponse(responseCode = "404", description = "Pauta não encontrada")
    @PostMapping("/{id}/abrir")
    public ResponseEntity<PautaDTO> abrirSessao(@PathVariable Long id,
                                                @RequestParam(required = false) Integer duracaoMinutos) {
        logger.info("Recebida requisição para abrir sessão: pautaID={}, duracaoMinutos={}", id, duracaoMinutos);
        Pauta pauta = pautaService.abrirSessao(id, duracaoMinutos);
        logger.info("Sessão aberta com sucesso: pautaID={}", pauta.getId());
        return ResponseEntity.ok(new PautaDTO(pauta.getId(), pauta.getTitulo()));
    }

    @Operation(summary = "Registra um voto")
    @ApiResponse(responseCode = "200", description = "Voto registrado com sucesso",
            content = @Content(schema = @Schema(implementation = VotoDTO.class)))
    @ApiResponse(responseCode = "400", description = "Associado não autorizado a votar")
    @ApiResponse(responseCode = "404", description = "Pauta não encontrada")
    @PostMapping("/{id}/votos")
    public ResponseEntity<VotoDTO> votar(@PathVariable Long id,
                                         @Valid @RequestBody VotacaoRequestDTO request) {
        logger.info("Recebida requisição de voto: pautaID={}, associadoID={}, escolha={}",
                id, request.associadoId(), request.escolha());
        Voto voto = votoService.registrarVoto(id, request.associadoId(), request.escolha());
        logger.info("Voto registrado com sucesso: pautaID={}, associadoID={}, escolha={}",
                id, voto.getAssociadoId(), voto.getEscolha());
        return ResponseEntity.ok(new VotoDTO(voto.getAssociadoId(), voto.getEscolha()));
    }

    @Operation(summary = "Obtém o resultado da votação")
    @ApiResponse(responseCode = "200", description = "Resultado retornado com sucesso",
            content = @Content(schema = @Schema(implementation = String.class, example = "SIM=7, NAO=3")))
    @ApiResponse(responseCode = "404", description = "Pauta não encontrada")
    @GetMapping("/{id}/resultado")
    public ResponseEntity<String> resultado(@PathVariable Long id) {
        logger.info("Recebida requisição de resultado: pautaID={}", id);
        String resultado = votoService.calcularResultado(id);
        logger.info("Resultado retornado: pautaID={}, resultado={}", id, resultado);
        return ResponseEntity.ok(resultado);
    }
}
