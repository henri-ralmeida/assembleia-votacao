package com.henrique.votacao.domain;

import jakarta.persistence.*;

@Entity
public class Pauta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String tituloPauta;

    private String abertura;
    private String fechamento;
    private Integer duracaoMinutos;

    // Construtor padrão
    public Pauta() {}

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTituloPauta() { return tituloPauta; }
    public void setTituloPauta(String tituloPauta) { this.tituloPauta = tituloPauta; }

    public String getAbertura() { return abertura; }
    public void setAbertura(String abertura) { this.abertura = abertura; }

    public String getFechamento() { return fechamento; }
    public void setFechamento(String fechamento) { this.fechamento = fechamento; }

    public Integer getDuracaoMinutos() { return duracaoMinutos; }
    public void setDuracaoMinutos(Integer duracaoMinutos) { this.duracaoMinutos = duracaoMinutos; }
}