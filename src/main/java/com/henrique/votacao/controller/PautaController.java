package com.henrique.votacao.controller;

import com.henrique.votacao.client.CpfClientFake;
import com.henrique.votacao.domain.Pauta;
import com.henrique.votacao.domain.Voto;
import com.henrique.votacao.dto.PautaDTO;
import com.henrique.votacao.dto.VotoDTO;
import com.henrique.votacao.dto.VotoRequestDTO;
import com.henrique.votacao.exception.BusinessException;
import com.henrique.votacao.service.PautaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Pautas", description = "Endpoints para gerenciamento de Pautas e Votações")
@RestController
@RequestMapping("/api/v1/pautas")
public class PautaController {

    private static final Logger logger = LoggerFactory.getLogger(PautaController.class);

    private final PautaService pautaService;
    private final CpfClientFake cpfClient;

    public PautaController(PautaService pautaService, CpfClientFake cpfClient) {
        this.pautaService = pautaService;
        this.cpfClient = cpfClient;
    }

    @Operation(
            summary = "Cria uma nova pauta",
            description = "Registra uma pauta com título no sistema."
    )
    @ApiResponse(responseCode = "200", description = "Pauta criada com sucesso",
            content = @Content(schema = @Schema(implementation = PautaDTO.class)))
    @PostMapping
    public ResponseEntity<PautaDTO> criarPauta(@RequestBody Pauta pauta) {
        logger.info("Request para criar pauta recebida: {}", pauta);
        Pauta criada = pautaService.criarPauta(pauta);
        logger.info("Pauta criada com sucesso: {}", criada);
        return ResponseEntity.ok(new PautaDTO(criada.getId(), criada.getTitulo()));
    }

    @Operation(
            summary = "Abre sessão de votação",
            description = "Inicia a contagem de votos para a pauta."
    )
    @ApiResponse(responseCode = "200", description = "Sessão aberta com sucesso",
            content = @Content(schema = @Schema(implementation = PautaDTO.class)))
    @ApiResponse(responseCode = "404", description = "Pauta não encontrada")
    @PostMapping("/{id}/abrir")
    public ResponseEntity<PautaDTO> abrirSessao(@PathVariable Long id,
                                                @RequestParam(required = false) Integer duracaoMinutos) {
        logger.info("Request para abrir sessão recebida: pautaID={}, duracaoMinutos={}", id, duracaoMinutos);
        Pauta pauta = pautaService.abrirSessao(id, duracaoMinutos);
        logger.info("Sessão aberta com sucesso: pautaID={}", id);
        return ResponseEntity.ok(new PautaDTO(pauta.getId(), pauta.getTitulo()));
    }

    @Operation(
            summary = "Registra um voto",
            description = "Permite a um associado votar (SIM ou NAO) em uma pauta aberta."
    )
    @ApiResponse(responseCode = "200", description = "Voto registrado com sucesso",
            content = @Content(schema = @Schema(implementation = VotoDTO.class)))
    @ApiResponse(responseCode = "400", description = "Associado não autorizado a votar ou sessão fechada")
    @PostMapping("/{id}/votos")
    public ResponseEntity<VotoDTO> votar(@PathVariable Long id,
                                         @RequestBody VotoRequestDTO request) {
        logger.info("Request para votar recebida: pautaID={}, associadoID={}, escolha={}",
                id, request.associadoId(), request.escolha());

        String status = cpfClient.verificarCpf(request.associadoId());
        if (!"ABLE_TO_VOTE".equals(status)) {
            logger.warn("Associado não autorizado a votar: pautaID={}, associadoID={}", id, request.associadoId());
            throw new BusinessException("Associado não autorizado a votar");
        }

        Voto voto = pautaService.votar(id, request.associadoId(), request.escolha());
        logger.info("Voto registrado com sucesso: {}", voto);
        return ResponseEntity.ok(new VotoDTO(voto.getAssociadoId(), voto.getEscolha()));
    }

    @Operation(
            summary = "Obtém o resultado da votação",
            description = "Retorna o resultado final (contagem de SIM/NAO) para uma pauta."
    )
    @ApiResponse(responseCode = "200", description = "Resultado retornado com sucesso",
            content = @Content(schema = @Schema(implementation = String.class, example = "Total: 10, Sim: 7, Não: 3")))
    @ApiResponse(responseCode = "404", description = "Pauta não encontrada")
    @GetMapping("/{id}/resultado")
    public ResponseEntity<String> resultado(@PathVariable Long id) {
        logger.info("Request para consultar resultado recebida: pautaID={}", id);
        String resultado = pautaService.resultadoVotacao(id);
        logger.info("Resultado retornado: {}", resultado);
        return ResponseEntity.ok(resultado);
    }
}
