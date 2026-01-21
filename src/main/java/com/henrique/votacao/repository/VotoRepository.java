package com.henrique.votacao.repository;

import com.henrique.votacao.domain.model.pauta.Pauta;
import com.henrique.votacao.domain.model.voto.Escolha;
import com.henrique.votacao.domain.model.voto.Voto;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repositório para operações de persistência de Voto.
 * 
 * @author Henrique
 * @since 1.0
 */
@Repository
public interface VotoRepository extends JpaRepository<Voto, Long> {
    
    /**
     * Verifica se já existe um voto do CPF informado na pauta.
     * 
     * @param cpfId CPF do associado
     * @param pautaId ID da pauta
     * @return true se já votou, false caso contrário
     */
    @Query("SELECT COUNT(v) > 0 FROM Voto v WHERE v.cpf.numero = :cpfId AND v.pauta.id = :pautaId")
    boolean existsBycpfIdAndPautaId(@Param("cpfId") String cpfId, @Param("pautaId") Long pautaId);

    /**
     * Conta quantos votos foram registrados para uma pauta com determinada escolha.
     * 
     * @param pauta pauta da votação
     * @param escolha escolha do voto (SIM/NAO)
     * @return quantidade de votos
     */
    long countByPautaAndEscolha(Pauta pauta, Escolha escolha);
}
