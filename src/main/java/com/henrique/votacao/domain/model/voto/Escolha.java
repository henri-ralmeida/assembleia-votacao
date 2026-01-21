package com.henrique.votacao.domain.model.voto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Enum representando as opções de voto.
 * 
 * @author Henrique
 * @since 1.0
 */
@Schema(description = "Escolha do voto")
public enum Escolha {
    
    /**
     * Voto favorável à pauta.
     */
    SIM,
    
    /**
     * Voto contrário à pauta.
     */
    NAO
}
