package mmu;

import config.Configuracao;

/**
 * Implementa uma tabela de páginas genérica com 1 a 3 níveis, dividindo os bits
 * da VPN igualmente entre os níveis e armazenando folhas com
 * EntradaTabelaPagina.
 */
public class TabelaPaginas {
  private final int niveis;
  private final int bitsVPN;
  private final int[] bitsPorNivel;
  private final Object raiz;

  /** Constrói toda a hierarquia da tabela de páginas conforme a configuração. */
  public TabelaPaginas(Configuracao config) {
    this.niveis = config.getNiveisTabelaPaginas();
    this.bitsVPN = config.getBitsEnderecoVirtual() - config.getBitsDeslocamentoPagina();
    this.bitsPorNivel = calculaBitsPorNivel();
    this.raiz = criaNivel(0);
  }

  /**
   * Distribui os bits da VPN pelos níveis, mantendo a diferença no máximo em 1
   * bit entre níveis adjacentes.
   */
  private int[] calculaBitsPorNivel() {
    int[] bits = new int[niveis];
    int base = bitsVPN / niveis;
    int resto = bitsVPN % niveis;
    for (int i = 0; i < niveis; i++) {
      bits[i] = base + (i < resto ? 1 : 0);
    }
    return bits;
  }

  /** Recursivamente instancia a estrutura de níveis até chegar nas folhas. */
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

  /**
   * Decompõe uma VPN nos índices de cada nível (similar ao hardware real).
   */
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

  /** Percorre a hierarquia e retorna a folha referente à VPN fornecida. */
  private EntradaTabelaPagina acessarEntrada(int paginaVirtual) {
    int[] indices = extraiIndices(paginaVirtual);
    Object atual = raiz;
    for (int i = 0; i < niveis - 1; i++) {
      atual = ((Object[]) atual)[indices[i]];
    }
    EntradaTabelaPagina[] folhas = (EntradaTabelaPagina[]) atual;
    return folhas[indices[niveis - 1]];
  }

  /** API pública para obter a entrada completa de uma VPN. */
  public EntradaTabelaPagina getEntrada(int paginaVirtual) {
    return acessarEntrada(paginaVirtual);
  }

  /**
   * @return moldura física caso a VPN esteja mapeada ou -1 caso contrário.
   */
  public int obtemMoldura(int paginaVirtual) {
    EntradaTabelaPagina e = getEntrada(paginaVirtual);
    if (e.isValida()) {
      return e.getMoldura();
    }
    return -1;
  }

  /** Grava um novo mapeamento VPN -> moldura. */
  public void mapeiaPagina(int paginaVirtual, int moldura) {
    EntradaTabelaPagina e = getEntrada(paginaVirtual);
    e.setMoldura(moldura);
    e.setValida(true);
  }

  /** Invalida uma entrada previamente mapeada. */
  public void desmapeiaPagina(int paginaVirtual) {
    EntradaTabelaPagina e = getEntrada(paginaVirtual);
    e.setValida(false);
    e.setMoldura(-1);
  }

  /**
   * Procura qual VPN ocupa determinada moldura, útil para substituição.
   */
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

  /** @return total de páginas virtuais possíveis. */
  public int getNumeroPaginas() {
    return 1 << bitsVPN;
  }

  /** Imprime todas as entradas (útil para depuração/relatório final). */
  public void imprimirTabela(java.io.PrintWriter out) {
    int total = getNumeroPaginas();
    for (int vpn = 0; vpn < total; vpn++) {
      EntradaTabelaPagina e = getEntrada(vpn);
      out.printf("VPN=%d VALID=%b FRAME=%d%n", vpn, e.isValida(), e.getMoldura());
    }
  }
}
