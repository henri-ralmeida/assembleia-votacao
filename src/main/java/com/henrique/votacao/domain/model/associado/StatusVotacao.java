package com.henrique.votacao.domain.model.associado;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Representa o status de votação de um associado verificado pelo sistema externo.
 * 
 * @author Henrique
 * @since 1.0
 */
@Schema(description = "Status de autorização para votar")
public enum StatusVotacao {
    
    /**
     * Associado autorizado a votar.
     */
    ABLE_TO_VOTE,
    
    /**
     * Associado não autorizado a votar.
     */
    UNABLE_TO_VOTE
}
