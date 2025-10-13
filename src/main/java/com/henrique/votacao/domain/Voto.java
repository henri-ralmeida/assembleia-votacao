package com.henrique.votacao.domain;

import jakarta.persistence.*;

@Entity
public class Voto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String associadoId; // ID
    private Escolha escolha;     // "SIM" ou "NAO"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pauta_id")
    private Pauta pauta;

    // Construtor
    public Voto() {}

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAssociadoId() { return associadoId; }
    public void setAssociadoId(String associadoId) { this.associadoId = associadoId; }

    public Escolha getEscolha() { return escolha; }
    public void setEscolha(Escolha escolha) { this.escolha = escolha; }

    public Pauta getPauta() { return pauta; }
    public void setPauta(Pauta pauta) { this.pauta = pauta; }
}
