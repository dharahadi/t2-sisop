package mmu;

import config.Configuracao;

public class TabelaPaginas {
  private final Configuracao config;
  private final int niveis;
  private final int bitsVPN;
  private final int[] bitsPorNivel;
  private final Object raiz;

  public TabelaPaginas(Configuracao config) {
    this.config = config;
    this.niveis = config.getNiveisTabelaPaginas();
    this.bitsVPN = config.getBitsEnderecoVirtual() - config.getBitsDeslocamentoPagina();
    this.bitsPorNivel = calculaBitsPorNivel();
    this.raiz = criaNivel(0);
  }

  private int[] calculaBitsPorNivel() {
    int[] bits = new int[niveis];
    int base = bitsVPN / niveis;
    int resto = bitsVPN % niveis;
    for (int i = 0; i < niveis; i++) {
      bits[i] = base + (i < resto ? 1 : 0);
    }
    return bits;
  }

  private Object criaNivel(int nivel) {
    int tamanho = 1 << bitsPorNivel[nivel];
    if (nivel == niveis - 1) {
      EntradaTabelaPagina[] folhas = new EntradaTabelaPagina[tamanho];
      for (int i = 0; i < tamanho; i++) {
        folhas[i] = new EntradaTabelaPagina();
      }
      return folhas;
    }
    Object[] proximoNivel = new Object[tamanho];
    for (int i = 0; i < tamanho; i++) {
      proximoNivel[i] = criaNivel(nivel + 1);
    }
    return proximoNivel;
  }

  private int[] extraiIndices(int paginaVirtual) {
    int[] indices = new int[niveis];
    int shift = bitsVPN;
    for (int i = 0; i < niveis; i++) {
      shift -= bitsPorNivel[i];
      int mascara = (1 << bitsPorNivel[i]) - 1;
      indices[i] = (paginaVirtual >> shift) & mascara;
    }
    return indices;
  }

  private EntradaTabelaPagina acessarEntrada(int paginaVirtual) {
    int[] indices = extraiIndices(paginaVirtual);
    Object atual = raiz;
    for (int i = 0; i < niveis - 1; i++) {
      atual = ((Object[]) atual)[indices[i]];
    }
    EntradaTabelaPagina[] folhas = (EntradaTabelaPagina[]) atual;
    return folhas[indices[niveis - 1]];
  }

  public EntradaTabelaPagina getEntrada(int paginaVirtual) {
    return acessarEntrada(paginaVirtual);
  }

  public int obtemMoldura(int paginaVirtual) {
    EntradaTabelaPagina e = getEntrada(paginaVirtual);
    if (e.isValida()) {
      return e.getMoldura();
    }
    return -1;
  }

  public void mapeiaPagina(int paginaVirtual, int moldura) {
    EntradaTabelaPagina e = getEntrada(paginaVirtual);
    e.setMoldura(moldura);
    e.setValida(true);
  }

  public void desmapeiaPagina(int paginaVirtual) {
    EntradaTabelaPagina e = getEntrada(paginaVirtual);
    e.setValida(false);
    e.setMoldura(-1);
  }

  public int encontraPaginaPorMoldura(int moldura) {
    int total = getNumeroPaginas();
    for (int vpn = 0; vpn < total; vpn++) {
      EntradaTabelaPagina e = getEntrada(vpn);
      if (e.isValida() && e.getMoldura() == moldura) {
        return vpn;
      }
    }
    return -1;
  }

  public int getNumeroPaginas() {
    return 1 << bitsVPN;
  }

  public void imprimirTabela(java.io.PrintWriter out) {
    int total = getNumeroPaginas();
    for (int vpn = 0; vpn < total; vpn++) {
      EntradaTabelaPagina e = getEntrada(vpn);
      out.printf("VPN=%d VALID=%b FRAME=%d%n", vpn, e.isValida(), e.getMoldura());
    }
  }
}
