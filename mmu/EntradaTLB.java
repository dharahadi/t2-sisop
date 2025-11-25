package mmu;

/**
 * Entrada individual da TLB. Armazena uma VPN, a moldura correspondente e o
 * instante usado pelo algoritmo LRU.
 */
public class EntradaTLB {
    private int paginaVirtual;
    private int moldura;
    private boolean valida;
    private long instanteInsercao;

    /** Cria uma entrada inválida pronta para reutilização. */
    public EntradaTLB() {
        this.valida = false;
    }

    /** @return a página virtual indexada por esta entrada. */
    public int getPaginaVirtual() {
        return paginaVirtual;
    }

    /** Atualiza a VPN armazenada na entrada. */
    public void setPaginaVirtual(int paginaVirtual) {
        this.paginaVirtual = paginaVirtual;
    }

    /** @return moldura física armazenada. */
    public int getMoldura() {
        return moldura;
    }

    /** Define a moldura física mapeada para a VPN atual. */
    public void setMoldura(int moldura) {
        this.moldura = moldura;
    }

    public boolean isValida() {
        return valida;
    }

    /** Ativa ou invalida a entrada. */
    public void setValida(boolean valida) {
        this.valida = valida;
    }

    /** Instante usado para desempate no algoritmo LRU. */
    public long getInstanteInsercao() {
        return instanteInsercao;
    }

    public void setInstanteInsercao(long instanteInsercao) {
        this.instanteInsercao = instanteInsercao;
    }
}
