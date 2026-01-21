package com.henrique.votacao.domain.exception;

/**
 * Exceção lançada quando se tenta abrir uma sessão sem que a pauta
 * tenha sido criada antes.
 * 
 * @author Henrique
 * @since 1.0
 */
public class SessaoNaoAbertaException extends DomainException {
    
    public SessaoNaoAbertaException() {
        super("Sessão de votação não foi aberta");
    }
}
