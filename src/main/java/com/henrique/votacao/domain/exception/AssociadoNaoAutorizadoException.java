package com.henrique.votacao.domain.exception;

/**
 * Exceção lançada quando um associado não está autorizado a votar.
 * 
 * @author Henrique
 * @since 1.0
 */
public class AssociadoNaoAutorizadoException extends DomainException {
    
    public AssociadoNaoAutorizadoException() {
        super("Associado não autorizado a votar");
    }
    
    public AssociadoNaoAutorizadoException(String cpf) {
        super("Associado com CPF " + cpf + " não está autorizado a votar");
    }
}
