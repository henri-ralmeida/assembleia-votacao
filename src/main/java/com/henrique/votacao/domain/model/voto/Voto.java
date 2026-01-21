package com.henrique.votacao.domain.model.voto;

import com.henrique.votacao.domain.model.pauta.Pauta;

import jakarta.persistence.*;

import org.springframework.util.Assert;

/**
 * Entity representando um voto de um associado em uma pauta.
 * 
 * <p>Cada voto é único por CPF e pauta, garantido por constraint no banco.
 * 
 * @author Henrique
 * @since 1.0
 */
@Entity
@Table(name = "voto", 
    indexes = {
        @Index(name = "idx_cpf_pauta", columnList = "cpf_id, pauta_id"),
        @Index(name = "idx_pauta_escolha", columnList = "pauta_id, escolha")
    },
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_cpf_pauta", columnNames = {"cpf_id", "pauta_id"})
    }
)
public class Voto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Cpf cpf;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Escolha escolha;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pauta_id", nullable = false)
    private Pauta pauta;

    /**
     * Construtor padrão JPA.
     * Não utilizar diretamente.
     */
    protected Voto() {
    }

    /**
     * Cria um novo voto com validação.
     * 
     * @param cpf CPF do associado
     * @param escolha escolha do voto (SIM/NAO)
     * @param pauta pauta sendo votada
     * @throws IllegalArgumentException se algum parâmetro for nulo
     */
    public Voto(Cpf cpf, Escolha escolha, Pauta pauta) {
        Assert.notNull(cpf, "CPF não pode ser nulo");
        Assert.notNull(escolha, "Escolha não pode ser nula");
        Assert.notNull(pauta, "Pauta não pode ser nula");
        
        this.cpf = cpf;
        this.escolha = escolha;
        this.pauta = pauta;
    }

    // Getters

    public Long getId() {
        return id;
    }

    public Cpf getCpf() {
        return cpf;
    }

    /**
     * Retorna o CPF como String para compatibilidade.
     * 
     * @return número do CPF
     */
    public String getCpfId() {
        return cpf != null ? cpf.getNumero() : null;
    }

    public Escolha getEscolha() {
        return escolha;
    }

    public Pauta getPauta() {
        return pauta;
    }

    @Override
    public String toString() {
        return "Voto{" +
                "id=" + id +
                ", cpf=" + cpf +
                ", escolha=" + escolha +
                ", pautaId=" + (pauta != null ? pauta.getId() : null) +
                '}';
    }
}
