package mmu;

/** Representa uma entrada folha da tabela de páginas (VPN -> moldura). */
public class EntradaTabelaPagina {
  private int moldura;
  private boolean valida;

  /** Inicialmente a entrada é inválida e aponta para nenhuma moldura. */
  public EntradaTabelaPagina() {
    this.moldura = -1;
    this.valida = false;
  }

  /** @return moldura física associada à página virtual. */
  public int getMoldura() {
    return moldura;
  }

  /** Define a moldura onde a página está carregada. */
  public void setMoldura(int moldura) {
    this.moldura = moldura;
  }

  /** @return true se a entrada contém um mapeamento válido. */
  public boolean isValida() {
    return valida;
  }

  /** Ativa/desativa o mapeamento. */
  public void setValida(boolean valida) {
    this.valida = valida;
  }
}
