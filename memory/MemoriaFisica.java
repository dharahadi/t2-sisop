package memory;

/**
 * Simula as molduras físicas da RAM, guardando o endereço virtual atualmente
 * mapeado em cada uma e o instante do último acesso, para viabilizar LRU.
 */
public class MemoriaFisica {
  private final long[] conteudoMolduras;
  private final long[] instanteUltimoAcesso;

  /** Inicializa o vetor de molduras marcando todas como livres. */
  public MemoriaFisica(int numeroMolduras) {
    this.conteudoMolduras = new long[numeroMolduras];
    this.instanteUltimoAcesso = new long[numeroMolduras];

    for (int i = 0; i < numeroMolduras; i++) {
      conteudoMolduras[i] = -1; // livre
      instanteUltimoAcesso[i] = 0;
    }
  }

  public long[] getConteudoMolduras() {
    return conteudoMolduras;
  }

  /** Atualiza somente o instante de acesso para uma moldura já ocupada. */
  public void atualizaAcesso(int moldura, long instante) {
    instanteUltimoAcesso[moldura] = instante;
  }

  /**
   * Registra o endereço virtual (início da página) que passou a ocupar a moldura.
   */
  public void setaConteudo(int moldura, long enderecoVirtual, long instante) {
    conteudoMolduras[moldura] = enderecoVirtual;
    instanteUltimoAcesso[moldura] = instante;
  }

  /**
   * Seleciona a moldura que será usada pela próxima página.
   *
   * <p>
   * Primeiramente procura espaços livres; caso não existam, escolhe a moldura
   * com menor instante registrado (LRU).
   */
  public int selecionaMolduraParaUso(long instanteAtual) {
    // tenta achar livre
    for (int i = 0; i < conteudoMolduras.length; i++) {
      if (conteudoMolduras[i] == -1) {
        instanteUltimoAcesso[i] = instanteAtual;
        return i;
      }
    }

    // nenhum livre => LRU
    int indiceVitima = 0;
    long menorInstante = instanteUltimoAcesso[0];

    for (int i = 1; i < conteudoMolduras.length; i++) {
      if (instanteUltimoAcesso[i] < menorInstante) {
        menorInstante = instanteUltimoAcesso[i];
        indiceVitima = i;
      }
    }

    instanteUltimoAcesso[indiceVitima] = instanteAtual;
    return indiceVitima;
  }

  public int getNumeroMolduras() {
    return conteudoMolduras.length;
  }
}
