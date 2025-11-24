package memory;

public class MemoriaFisica {
  private final long[] conteudoMolduras; // Armazena endereço virtual completo (ou -1 se livre)
  private final long[] instanteUltimoAcesso;

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

  public void atualizaAcesso(int moldura, long instante) {
    instanteUltimoAcesso[moldura] = instante;
  }

  public void setaConteudo(int moldura, long enderecoVirtual, long instante) {
    conteudoMolduras[moldura] = enderecoVirtual;
    instanteUltimoAcesso[moldura] = instante;
  }

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
