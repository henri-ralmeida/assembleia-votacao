package com.henrique.votacao.domain.exception;

/**
 * Exceção lançada quando uma pauta já existe com o mesmo título.
 * 
 * @author Henrique
 * @since 1.0
 */
public class PautaDuplicadaException extends DomainException {
    
    public PautaDuplicadaException() {
        super("Já existe uma pauta com esse título");
    }
    
    public PautaDuplicadaException(String titulo) {
        super("Já existe uma pauta com o título: " + titulo);
    }
}
