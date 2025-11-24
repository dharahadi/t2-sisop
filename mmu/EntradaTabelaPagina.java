package mmu;

public class EntradaTabelaPagina {
  private int moldura;
  private boolean valida;

  public EntradaTabelaPagina() {
    this.moldura = -1;
    this.valida = false;
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
}
