package com.henrique.votacao.domain.exception;

/**
 * Exceção lançada quando se tenta votar em uma sessão que não foi aberta
 * ou que já foi encerrada.
 * 
 * @author Henrique
 * @since 1.0
 */
public class SessaoFechadaException extends DomainException {
    
    public SessaoFechadaException() {
        super("Sessão de votação fechada");
    }
    
    public SessaoFechadaException(String message) {
        super(message);
    }
}
