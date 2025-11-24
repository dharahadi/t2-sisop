package config;

public class Configuracao {
  private final int bitsEnderecoVirtual;
  private final int bitsEnderecoFisico;
  private final int bitsDeslocamentoPagina;
  private final int bitsEntradasTLB;
  private final int niveisTabelaPaginas;

  private final int bitsTamanhoText;
  private final int bitsTamanhoData;
  private final int bitsTamanhoStack;

  private final String arquivoEntrada;
  private final String arquivoSaida;

  public Configuracao(int bitsEnderecoVirtual,
      int bitsEnderecoFisico,
      int bitsDeslocamentoPagina,
      int bitsEntradasTLB,
      int niveisTabelaPaginas,
      int bitsTamanhoText,
      int bitsTamanhoData,
      int bitsTamanhoStack,
      String arquivoEntrada,
      String arquivoSaida) {
    if (bitsEnderecoVirtual < bitsEnderecoFisico) {
      throw new IllegalArgumentException(
          "Espaço de endereços virtuais deve ser >= ao tamanho da memória física");
    }
    if (niveisTabelaPaginas < 1 || niveisTabelaPaginas > 3) {
      throw new IllegalArgumentException("Níveis da tabela de páginas devem ser 1, 2 ou 3");
    }
    this.bitsEnderecoVirtual = bitsEnderecoVirtual;
    this.bitsEnderecoFisico = bitsEnderecoFisico;
    this.bitsDeslocamentoPagina = bitsDeslocamentoPagina;
    this.bitsEntradasTLB = bitsEntradasTLB;
    this.niveisTabelaPaginas = niveisTabelaPaginas;
    this.bitsTamanhoText = bitsTamanhoText;
    this.bitsTamanhoData = bitsTamanhoData;
    this.bitsTamanhoStack = bitsTamanhoStack;
    this.arquivoEntrada = arquivoEntrada;
    this.arquivoSaida = arquivoSaida;
  }

  // Ajusta esses valores conforme o enunciado/professor
  public static Configuracao configuracaoPadrao() {
    return new Configuracao(
        16, // bits do endereço virtual
        14, // bits do endereço físico
        12, // bits de deslocamento (página 4KB)
        3, // 2^3 = 8 entradas TLB
        1, // níveis tabela páginas
        12, // 2^12 bytes .text
        12, // 2^12 bytes .data
        12, // 2^12 bytes .stack
        "enderecos_entrada.txt",
        "saida_simulador.txt");
  }

  public int getBitsEnderecoVirtual() {
    return bitsEnderecoVirtual;
  }

  public int getBitsEnderecoFisico() {
    return bitsEnderecoFisico;
  }

  public int getBitsDeslocamentoPagina() {
    return bitsDeslocamentoPagina;
  }

  public int getEntradasTLB() {
    return 1 << bitsEntradasTLB;
  }

  public int getBitsEntradasTLB() {
    return bitsEntradasTLB;
  }

  public int getNiveisTabelaPaginas() {
    return niveisTabelaPaginas;
  }

  public long getTamanhoText() {
    return 1L << bitsTamanhoText;
  }

  public long getTamanhoData() {
    return 1L << bitsTamanhoData;
  }

  public long getTamanhoStack() {
    return 1L << bitsTamanhoStack;
  }

  public long getTamanhoBss() {
    return (getTamanhoText() + getTamanhoData() + getTamanhoStack()) * 3;
  }

  public String getArquivoEntrada() {
    return arquivoEntrada;
  }

  public String getArquivoSaida() {
    return arquivoSaida;
  }

  // Derivados

  public int getTamanhoPagina() {
    return 1 << bitsDeslocamentoPagina;
  }

  public int getNumeroPaginasVirtuais() {
    int bitsPagina = bitsEnderecoVirtual - bitsDeslocamentoPagina;
    return 1 << bitsPagina;
  }

  public int getNumeroMoldurasFisicas() {
    int bitsMoldura = bitsEnderecoFisico - bitsDeslocamentoPagina;
    return 1 << bitsMoldura;
  }

  public long getTamanhoEspacoEnderecoVirtual() {
    return 1L << bitsEnderecoVirtual;
  }
}
