package com.henrique.votacao.domain.exception;

/**
 * Exceção lançada quando uma pauta não é encontrada.
 * 
 * @author Henrique
 * @since 1.0
 */
public class PautaNaoEncontradaException extends DomainException {
    
    public PautaNaoEncontradaException() {
        super("Pauta não encontrada");
    }
    
    public PautaNaoEncontradaException(String titulo) {
        super("Pauta não encontrada: " + titulo);
    }
}
