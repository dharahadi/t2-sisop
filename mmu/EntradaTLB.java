package mmu;

public class EntradaTLB {
    private int paginaVirtual;
    private int moldura;
    private boolean valida;
    private long instanteInsercao;

    public EntradaTLB() {
        this.valida = false;
    }

    public int getPaginaVirtual() {
        return paginaVirtual;
    }

    public void setPaginaVirtual(int paginaVirtual) {
        this.paginaVirtual = paginaVirtual;
    }

    public int getMoldura() {
        return moldura;
    }

    public void setMoldura(int moldura) {
        this.moldura = moldura;
    }

    public boolean isValida() {
        return valida;
    }

    public void setValida(boolean valida) {
        this.valida = valida;
    }

    public long getInstanteInsercao() {
        return instanteInsercao;
    }

    public void setInstanteInsercao(long instanteInsercao) {
        this.instanteInsercao = instanteInsercao;
    }
}
