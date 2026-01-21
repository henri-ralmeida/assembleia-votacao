package com.henrique.votacao.domain.exception;

/**
 * Exceção lançada quando se tenta abrir uma sessão que já está aberta.
 * 
 * @author Henrique
 * @since 1.0
 */
public class SessaoJaAbertaException extends DomainException {
    
    public SessaoJaAbertaException() {
        super("Sessão de votação já foi aberta para esta pauta");
    }
}
