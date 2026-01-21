package com.henrique.votacao.repository;

import com.henrique.votacao.domain.model.pauta.Pauta;
import com.henrique.votacao.domain.model.pauta.TituloPauta;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositório para operações de persistência de Pauta.
 * 
 * @author Henrique
 * @since 1.0
 */
@Repository
public interface PautaRepository extends JpaRepository<Pauta, Long> {
    
    /**
     * Busca uma pauta pelo título.
     * 
     * @param titulo título da pauta
     * @return Optional com a pauta ou vazio se não encontrada
     */
    @Query("SELECT p FROM Pauta p WHERE p.titulo.valor = :titulo")
    Optional<Pauta> findByTituloPauta(@Param("titulo") String titulo);

    /**
     * Verifica se existe uma pauta com o título informado.
     * 
     * @param titulo título da pauta
     * @return true se existe, false caso contrário
     */
    @Query("SELECT COUNT(p) > 0 FROM Pauta p WHERE p.titulo.valor = :titulo")
    boolean existsByTituloPauta(@Param("titulo") String titulo);
}
