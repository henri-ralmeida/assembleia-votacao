package com.henrique.votacao.domain.exception;

/**
 * Exceção base para todas as exceções de domínio.
 * 
 * <p>Exceções de domínio representam violações de regras de negócio
 * e devem ser tratadas pela camada de apresentação (controller).
 * 
 * @author Henrique
 * @since 1.0
 */
public abstract class DomainException extends RuntimeException {
    
    protected DomainException(String message) {
        super(message);
    }
    
    protected DomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
