package com.henrique.votacao.domain;

import jakarta.persistence.*;

@Entity
public class Voto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String cpfId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Escolha escolha;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pauta_id")
    private Pauta pauta;

    // Construtor padrão
    public Voto() {}

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCpfId() { return cpfId; }
    public void setCpfId(String cpfId) { this.cpfId = cpfId; }

    public Escolha getEscolha() { return escolha; }
    public void setEscolha(Escolha escolha) { this.escolha = escolha; }

    public Pauta getPauta() { return pauta; }
    public void setPauta(Pauta pauta) { this.pauta = pauta; }
}
