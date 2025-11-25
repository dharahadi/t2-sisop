package mmu;

/**
 * Tradução simplificada de uma TLB associativa com política LRU.
 */
public class TLB {
  private final EntradaTLB[] entradas;

  /**
   * Instancia a TLB com a capacidade configurada preenchendo entradas inválidas.
   */
  public TLB(int capacidade) {
    this.entradas = new EntradaTLB[capacidade];
    for (int i = 0; i < capacidade; i++) {
      entradas[i] = new EntradaTLB();
    }
  }

  /**
   * Procura a VPN infromada e, em caso de hit, atualiza o instante para LRU.
   *
   * @return moldura traduzida ou -1 em caso de miss.
   */
  public int lookup(int paginaVirtual, long instanteAtual) {
    for (EntradaTLB e : entradas) {
      if (e.isValida() && e.getPaginaVirtual() == paginaVirtual) {
        // Atualiza instante para comportamento LRU correto
        e.setInstanteInsercao(instanteAtual);
        return e.getMoldura();
      }
    }
    return -1;
  }

  /**
   * Insere uma nova entrada ou atualiza a existente. Caso não haja vaga livre,
   * substitui a entrada mais antiga (menor instante).
   */
  public void insereOuAtualiza(int paginaVirtual, int moldura, long instanteAtual) {
    // já existe?
    for (EntradaTLB e : entradas) {
      if (e.isValida() && e.getPaginaVirtual() == paginaVirtual) {
        e.setMoldura(moldura);
        e.setInstanteInsercao(instanteAtual);
        return;
      }
    }

    // vaga livre?
    for (EntradaTLB e : entradas) {
      if (!e.isValida()) {
        e.setPaginaVirtual(paginaVirtual);
        e.setMoldura(moldura);
        e.setValida(true);
        e.setInstanteInsercao(instanteAtual);
        return;
      }
    }

    // substitui mais antiga
    EntradaTLB maisAntiga = entradas[0];
    for (EntradaTLB e : entradas) {
      if (e.getInstanteInsercao() < maisAntiga.getInstanteInsercao()) {
        maisAntiga = e;
      }
    }
    maisAntiga.setPaginaVirtual(paginaVirtual);
    maisAntiga.setMoldura(moldura);
    maisAntiga.setInstanteInsercao(instanteAtual);
    maisAntiga.setValida(true);
  }

  /** Invalida a entrada referente à VPN informada (usado em substituições). */
  public void invalidaEntrada(int paginaVirtual) {
    for (EntradaTLB e : entradas) {
      if (e.isValida() && e.getPaginaVirtual() == paginaVirtual) {
        e.setValida(false);
        e.setMoldura(-1);
        return;
      }
    }
  }

  public EntradaTLB[] getEntradas() {
    return entradas;
  }
}
