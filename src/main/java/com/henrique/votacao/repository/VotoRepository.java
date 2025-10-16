package com.henrique.votacao.repository;

import com.henrique.votacao.domain.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VotoRepository extends JpaRepository<Voto, Long> {
    boolean existsBycpfIdAndPautaId(String cpfId, Long pautaId);

    long countByPautaAndEscolha(Pauta pauta, Escolha escolha);
}
