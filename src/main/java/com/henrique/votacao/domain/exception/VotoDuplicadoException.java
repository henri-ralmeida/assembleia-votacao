package com.henrique.votacao.domain.exception;

/**
 * Exceção lançada quando um associado tenta votar mais de uma vez na mesma pauta.
 * 
 * @author Henrique
 * @since 1.0
 */
public class VotoDuplicadoException extends DomainException {
    
    public VotoDuplicadoException() {
        super("Associado já votou");
    }
    
    public VotoDuplicadoException(String cpf) {
        super("Associado com CPF " + cpf + " já votou nesta pauta");
    }
}
