package com.henrique.votacao.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Pauta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    private LocalDateTime abertura;
    private LocalDateTime fechamento;

    @OneToMany(mappedBy = "pauta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Voto> votos = new ArrayList<>();

    // Construtor padrão
    public Pauta() {}

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public LocalDateTime getAbertura() { return abertura; }
    public void setAbertura(LocalDateTime abertura) { this.abertura = abertura; }

    public LocalDateTime getFechamento() { return fechamento; }
    public void setFechamento(LocalDateTime fechamento) { this.fechamento = fechamento; }

    public List<Voto> getVotos() { return votos; }
    public void setVotos(List<Voto> votos) { this.votos = votos; }

    public void adicionarVoto(Voto voto) {
        votos.add(voto);
        voto.setPauta(this);
    }
}