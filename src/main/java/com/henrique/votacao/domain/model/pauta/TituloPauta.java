package com.henrique.votacao.domain.model.pauta;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.Objects;

/**
 * Value Object representando o título de uma pauta.
 * 
 * <p>Garante que o título seja válido (não nulo, não vazio) e único no sistema.
 * O título é utilizado como identificador de negócio da pauta para facilitar
 * o uso via API REST sem necessidade de conhecer IDs técnicos.
 * 
 * <p>Imutável e thread-safe.
 * 
 * @author Henrique
 * @since 1.0
 */
@Embeddable
public class TituloPauta {

    @Column(name = "titulo_pauta", nullable = false, unique = true, length = 500)
    private String valor;

    /**
     * Construtor padrão JPA.
     * Não utilizar diretamente - use o construtor com validação.
     */
    protected TituloPauta() {
    }

    /**
     * Cria um novo título de pauta com validação.
     * 
     * @param valor o título da pauta
     * @throws IllegalArgumentException se o título for nulo, vazio ou ultrapassar 500 caracteres
     */
    public TituloPauta(String valor) {
        validar(valor);
        this.valor = valor;
    }

    private void validar(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("O título da pauta não pode ser nulo ou vazio");
        }
        if (valor.length() > 500) {
            throw new IllegalArgumentException("O título da pauta não pode ultrapassar 500 caracteres");
        }
    }

    public String getValor() {
        return valor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TituloPauta that = (TituloPauta) o;
        return Objects.equals(valor, that.valor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valor);
    }

    @Override
    public String toString() {
        return valor;
    }
}
