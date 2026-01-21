package com.henrique.votacao.domain.model.voto;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.Objects;

/**
 * Value Object representando um CPF.
 * 
 * <p>Garante que o CPF possua exatamente 11 dígitos numéricos.
 * Para validação completa com dígitos verificadores, pode-se
 * implementar o algoritmo de validação de CPF futuramente.
 * 
 * <p>Imutável e thread-safe.
 * 
 * @author Henrique
 * @since 1.0
 */
@Embeddable
public class Cpf {

    @Column(name = "cpf_id", nullable = false, length = 11)
    private String numero;

    /**
     * Construtor padrão JPA.
     * Não utilizar diretamente - use o construtor com validação.
     */
    protected Cpf() {
    }

    /**
     * Cria um novo CPF com validação.
     * 
     * @param numero o número do CPF (11 dígitos)
     * @throws IllegalArgumentException se o CPF for inválido
     */
    public Cpf(String numero) {
        validar(numero);
        this.numero = numero;
    }

    private void validar(String numero) {
        if (numero == null || numero.isBlank()) {
            throw new IllegalArgumentException("O CPF não pode ser nulo ou vazio");
        }
        
        if (!numero.matches("\\d{11}")) {
            throw new IllegalArgumentException("O CPF deve conter exatamente 11 dígitos numéricos");
        }
    }

    public String getNumero() {
        return numero;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cpf cpf = (Cpf) o;
        return Objects.equals(numero, cpf.numero);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numero);
    }

    @Override
    public String toString() {
        return numero;
    }
}
