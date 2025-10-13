package com.henrique.votacao.service;

import com.henrique.votacao.domain.Escolha;
import com.henrique.votacao.domain.Pauta;
import com.henrique.votacao.domain.Voto;
import com.henrique.votacao.exception.BusinessException;
import com.henrique.votacao.exception.NotFoundException;
import com.henrique.votacao.repository.PautaRepository;
import com.henrique.votacao.repository.VotoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PautaService {

    private final PautaRepository pautaRepository;
    private final VotoRepository votoRepository;

    public PautaService(PautaRepository pautaRepository, VotoRepository votoRepository) {
        this.pautaRepository = pautaRepository;
        this.votoRepository = votoRepository;
    }

    // Criar Nova Pauta
    public Pauta criarPauta(Pauta pauta) {
        pauta.setAbertura(null);
        pauta.setFechamento(null);
        return pautaRepository.save(pauta);
    }

    // Abrir Sessão de Votação (tempo em minutos, default 1min)
    public Pauta abrirSessao(Long pautaId, Integer duracaoMinutos) {
        Pauta pauta = pautaRepository.findById(pautaId)
                .orElseThrow(() -> new NotFoundException("Pauta não encontrada"));

        pauta.setAbertura(LocalDateTime.now());
        pauta.setFechamento(LocalDateTime.now().plusMinutes(duracaoMinutos != null ? duracaoMinutos : 1));

        return pautaRepository.save(pauta);
    }

    // Registrar Voto
    public Voto votar(Long pautaId, String associadoId, String escolhaStr) {
        Pauta pauta = pautaRepository.findById(pautaId)
                .orElseThrow(() -> new NotFoundException("Pauta não encontrada"));

        LocalDateTime agora = LocalDateTime.now();
        if (pauta.getAbertura() == null || pauta.getFechamento() == null
                || agora.isBefore(pauta.getAbertura()) || agora.isAfter(pauta.getFechamento())) {
            throw new BusinessException("Sessão de votação fechada");
        }

        if (votoRepository.existsByAssociadoIdAndPautaId(associadoId, pautaId)) {
            throw new BusinessException("Associado já votou");
        }

        Escolha escolha;
        try {
            escolha = Escolha.fromString(escolhaStr);
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Escolha inválida: " + escolhaStr);
        }

        Voto voto = new Voto();
        voto.setAssociadoId(associadoId);
        voto.setEscolha(escolha);
        voto.setPauta(pauta);

        return votoRepository.save(voto);
    }

    // Contabilizar Votos
    public String resultadoVotacao(Long pautaId) {
        Pauta pauta = pautaRepository.findById(pautaId)
                .orElseThrow(() -> new NotFoundException("Pauta não encontrada"));

        List<Voto> votos = pauta.getVotos();
        long sim = votos.stream().filter(v -> v.getEscolha() == Escolha.SIM).count();
        long nao = votos.stream().filter(v -> v.getEscolha() == Escolha.NAO).count();


        return String.format("Resultado da Pauta %s: SIM=%d, NAO=%d", pauta.getTitulo(), sim, nao);
    }
}
