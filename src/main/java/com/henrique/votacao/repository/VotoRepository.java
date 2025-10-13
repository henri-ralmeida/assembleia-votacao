package com.henrique.votacao.repository;

import com.henrique.votacao.domain.Voto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VotoRepository extends JpaRepository<Voto, Long> {
    Optional<Voto> findByAssociadoIdAndPautaId(String associadoId, Long pautaId);

    boolean existsByAssociadoIdAndPautaId(String associadoId, Long pautaId);
}
