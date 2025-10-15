package com.henrique.votacao.repository;

import com.henrique.votacao.domain.Pauta;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PautaRepository extends JpaRepository<Pauta, Long> {
    Optional<Pauta> findByTitulo(String titulo);

    boolean existsByTitulo(String titulo);
}
