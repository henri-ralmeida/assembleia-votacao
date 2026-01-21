package com.henrique.votacao.domain.model.pauta;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Value Object representando uma sessão de votação.
 * 
 * <p>Encapsula as informações de abertura, fechamento e duração de uma sessão,
 * bem como a lógica para determinar se a sessão está aberta.
 * 
 * <p>Imutável e thread-safe.
 * 
 * @author Henrique
 * @since 1.0
 */
@Embeddable
public class SessaoVotacao {

    private static final DateTimeFormatter FORMATADOR = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH'h'mm'm'ss's'");
    private static final int DURACAO_PADRAO_MINUTOS = 1;

    @Column(name = "abertura")
    private String abertura;

    @Column(name = "fechamento")
    private String fechamento;

    @Column(name = "duracao_minutos")
    private Integer duracaoMinutos;

    /**
     * Construtor padrão JPA.
     * Não utilizar diretamente - use o factory method {@link #abrir(Integer)}.
     */
    protected SessaoVotacao() {
    }

    private SessaoVotacao(String abertura, String fechamento, Integer duracaoMinutos) {
        this.abertura = abertura;
        this.fechamento = fechamento;
        this.duracaoMinutos = duracaoMinutos;
    }

    /**
     * Factory method para criar uma nova sessão de votação.
     * 
     * @param duracaoMinutos duração em minutos (null ou menor que 1 resulta em 1 minuto)
     * @return nova sessão de votação aberta
     */
    public static SessaoVotacao abrir(Integer duracaoMinutos) {
        if (duracaoMinutos == null || duracaoMinutos < 1) {
            duracaoMinutos = DURACAO_PADRAO_MINUTOS;
        }

        LocalDateTime agora = LocalDateTime.now();
        String abertura = agora.format(FORMATADOR);
        String fechamento = agora.plusMinutes(duracaoMinutos).format(FORMATADOR);

        return new SessaoVotacao(abertura, fechamento, duracaoMinutos);
    }

    /**
     * Verifica se a sessão está atualmente aberta.
     * 
     * @return true se a sessão está aberta, false caso contrário
     */
    public boolean estaAberta() {
        if (abertura == null || fechamento == null) {
            return false;
        }

        LocalDateTime dataAbertura = LocalDateTime.parse(abertura, FORMATADOR);
        LocalDateTime dataFechamento = LocalDateTime.parse(fechamento, FORMATADOR);
        LocalDateTime agora = LocalDateTime.now();

        return !agora.isBefore(dataAbertura) && !agora.isAfter(dataFechamento);
    }

    public String getAbertura() {
        return abertura;
    }

    public String getFechamento() {
        return fechamento;
    }

    public Integer getDuracaoMinutos() {
        return duracaoMinutos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SessaoVotacao that = (SessaoVotacao) o;
        return Objects.equals(abertura, that.abertura) &&
                Objects.equals(fechamento, that.fechamento) &&
                Objects.equals(duracaoMinutos, that.duracaoMinutos);
    }

    @Override
    public int hashCode() {
        return Objects.hash(abertura, fechamento, duracaoMinutos);
    }

    @Override
    public String toString() {
        return "SessaoVotacao{" +
                "abertura='" + abertura + '\'' +
                ", fechamento='" + fechamento + '\'' +
                ", duracaoMinutos=" + duracaoMinutos +
                '}';
    }
}
