package mmu;

public class TLB {
  private final EntradaTLB[] entradas;

  public TLB(int capacidade) {
    this.entradas = new EntradaTLB[capacidade];
    for (int i = 0; i < capacidade; i++) {
      entradas[i] = new EntradaTLB();
    }
  }

  public int lookup(int paginaVirtual) {
    for (EntradaTLB e : entradas) {
      if (e.isValida() && e.getPaginaVirtual() == paginaVirtual) {
        return e.getMoldura();
      }
    }
    return -1;
  }

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

  public EntradaTLB[] getEntradas() {
    return entradas;
  }
}
