package com.henrique.votacao.controller;

import com.henrique.votacao.client.CpfClientFake;
import com.henrique.votacao.domain.Pauta;
import com.henrique.votacao.domain.Voto;
import com.henrique.votacao.exception.BusinessException;
import com.henrique.votacao.service.PautaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/pautas")
public class PautaController {

    private final PautaService pautaService;
    private final CpfClientFake cpfClient;

    public PautaController(PautaService pautaService, CpfClientFake cpfClient) {
        this.pautaService = pautaService;
        this.cpfClient = cpfClient;
    }

    // Criar Nova Pauta
    @PostMapping
    public ResponseEntity<Pauta> criarPauta(@RequestBody Pauta pauta) {
        return ResponseEntity.ok(pautaService.criarPauta(pauta));
    }

    // Abrir Sessão de Votação
    @PostMapping("/{id}/abrir")
    public ResponseEntity<Pauta> abrirSessao(
            @PathVariable Long id,
            @RequestParam(required = false) Integer duracaoMinutos) {
        return ResponseEntity.ok(pautaService.abrirSessao(id, duracaoMinutos));
    }

    // Registrar Voto
    @PostMapping("/{id}/votos")
    public ResponseEntity<Voto> votar(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {

        String associadoId = body.get("associadoId");
        String escolha = body.get("escolha");

        // Verifica CPF aleatoriamente
        String status = cpfClient.verificarCpf(associadoId);
        if (!"ABLE_TO_VOTE".equals(status)) {
            throw new BusinessException("Associado não autorizado a votar");
        }

        Voto voto = pautaService.votar(id, associadoId, escolha);
        return ResponseEntity.ok(voto);
    }

    // Resultado da Votação
    @GetMapping("/{id}/resultado")
    public ResponseEntity<String> resultado(@PathVariable Long id) {
        return ResponseEntity.ok(pautaService.resultadoVotacao(id));
    }
}
