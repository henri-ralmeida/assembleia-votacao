package com.henrique.votacao.domain.model.pauta;

import com.henrique.votacao.domain.exception.SessaoJaAbertaException;

import jakarta.persistence.*;

import org.springframework.util.Assert;

/**
 * Aggregate Root representando uma Pauta de votação.
 * 
 * <p>Uma pauta é um assunto a ser discutido e votado pelos associados.
 * Possui um título único que a identifica e pode ter uma sessão de votação aberta.
 * 
 * <p>Regras de negócio encapsuladas:
 * <ul>
 *   <li>Título deve ser único no sistema</li>
 *   <li>Sessão de votação só pode ser aberta uma vez</li>
 *   <li>Votos só podem ser registrados quando a sessão estiver aberta</li>
 * </ul>
 * 
 * @author Henrique
 * @since 1.0
 */
@Entity
@Table(name = "pauta", indexes = {
    @Index(name = "idx_titulo_pauta", columnList = "titulo_pauta")
})
public class Pauta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private TituloPauta titulo;

    @Embedded
    private SessaoVotacao sessao;

    /**
     * Construtor padrão JPA.
     * Não utilizar diretamente.
     */
    protected Pauta() {
    }

    /**
     * Cria uma nova pauta com título válido.
     * 
     * @param titulo o título da pauta
     * @throws IllegalArgumentException se o título for nulo
     */
    public Pauta(TituloPauta titulo) {
        Assert.notNull(titulo, "Título da pauta não pode ser nulo");
        this.titulo = titulo;
    }

    /**
     * Abre uma sessão de votação para esta pauta.
     * 
     * @param duracaoMinutos duração da sessão em minutos (null ou < 1 resulta em 1 minuto)
     * @throws SessaoJaAbertaException se a sessão já foi aberta
     */
    public void abrirSessao(Integer duracaoMinutos) {
        if (this.sessao != null) {
            throw new SessaoJaAbertaException();
        }
        this.sessao = SessaoVotacao.abrir(duracaoMinutos);
    }

    /**
     * Verifica se a pauta pode receber votos no momento atual.
     * 
     * @return true se a sessão está aberta, false caso contrário
     */
    public boolean podeReceberVoto() {
        return sessao != null && sessao.estaAberta();
    }

    /**
     * Verifica se a sessão foi aberta (independente de estar ativa).
     * 
     * @return true se a sessão foi aberta, false caso contrário
     */
    public boolean temSessaoAberta() {
        return sessao != null;
    }

    // Getters

    public Long getId() {
        return id;
    }

    public TituloPauta getTitulo() {
        return titulo;
    }

    /**
     * Retorna o título da pauta como String para compatibilidade.
     * 
     * @return o valor do título
     */
    public String getTituloPauta() {
        return titulo != null ? titulo.getValor() : null;
    }

    public SessaoVotacao getSessao() {
        return sessao;
    }

    /**
     * Retorna a abertura da sessão como String para compatibilidade.
     * 
     * @return data/hora de abertura ou null se não houver sessão
     */
    public String getAbertura() {
        return sessao != null ? sessao.getAbertura() : null;
    }

    /**
     * Retorna o fechamento da sessão como String para compatibilidade.
     * 
     * @return data/hora de fechamento ou null se não houver sessão
     */
    public String getFechamento() {
        return sessao != null ? sessao.getFechamento() : null;
    }

    /**
     * Retorna a duração da sessão para compatibilidade.
     * 
     * @return duração em minutos ou null se não houver sessão
     */
    public Integer getDuracaoMinutos() {
        return sessao != null ? sessao.getDuracaoMinutos() : null;
    }

    @Override
    public String toString() {
        return "Pauta{" +
                "id=" + id +
                ", titulo=" + titulo +
                ", sessao=" + sessao +
                '}';
    }
}
