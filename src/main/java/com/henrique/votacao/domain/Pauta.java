package com.henrique.votacao.domain;

import jakarta.persistence.*;

@Entity
public class Pauta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    private String abertura;
    private String fechamento;

    // Construtor padrão
    public Pauta() {}

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getAbertura() { return abertura; }
    public void setAbertura(String abertura) { this.abertura = abertura; }

    public String getFechamento() { return fechamento; }
    public void setFechamento(String fechamento) { this.fechamento = fechamento; }
}