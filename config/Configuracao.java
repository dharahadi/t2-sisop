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

  /**
   * Cria configuração a partir de um arquivo de configuração.
   * Formato do arquivo (uma linha por parâmetro):
   * bitsVirtual=16
   * bitsFisico=14
   * bitsPagina=12
   * bitsTLB=3
   * niveis=1
   * bitsText=12
   * bitsData=12
   * bitsStack=12
   * arquivoEntrada=enderecos_entrada.txt
   * arquivoSaida=saida_simulador.txt
   */
  public static Configuracao deArquivo(String caminhoArquivo) throws java.io.IOException {
    java.util.Properties props = new java.util.Properties();
    try (java.io.FileReader reader = new java.io.FileReader(caminhoArquivo)) {
      props.load(reader);
    }

    int bitsVirtual = Integer.parseInt(props.getProperty("bitsVirtual", "16"));
    int bitsFisico = Integer.parseInt(props.getProperty("bitsFisico", "14"));
    int bitsPagina = Integer.parseInt(props.getProperty("bitsPagina", "12"));
    int bitsTLB = Integer.parseInt(props.getProperty("bitsTLB", "3"));
    int niveis = Integer.parseInt(props.getProperty("niveis", "1"));
    int bitsText = Integer.parseInt(props.getProperty("bitsText", "12"));
    int bitsData = Integer.parseInt(props.getProperty("bitsData", "12"));
    int bitsStack = Integer.parseInt(props.getProperty("bitsStack", "12"));
    String arquivoEntrada = props.getProperty("arquivoEntrada", "enderecos_entrada.txt");
    String arquivoSaida = props.getProperty("arquivoSaida", "saida_simulador.txt");

    return new Configuracao(bitsVirtual, bitsFisico, bitsPagina, bitsTLB, niveis,
        bitsText, bitsData, bitsStack, arquivoEntrada, arquivoSaida);
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
    return (int) (1L << bitsEntradasTLB);
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

  /**
   * Calcula o tamanho do segmento .bss.
   * Conforme o enunciado: "Multiplicar o tamanho por 3".
   * Interpretação: multiplicar a soma dos tamanhos de .text, .data e .stack por
   * 3.
   */
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
    return (int) (1L << bitsDeslocamentoPagina);
  }

  public int getNumeroPaginasVirtuais() {
    int bitsPagina = bitsEnderecoVirtual - bitsDeslocamentoPagina;
    if (bitsPagina >= 31) {
      throw new ArithmeticException("Overflow: bitsPagina muito grande para int");
    }
    return (int) (1L << bitsPagina);
  }

  public int getNumeroMoldurasFisicas() {
    int bitsMoldura = bitsEnderecoFisico - bitsDeslocamentoPagina;
    if (bitsMoldura >= 31) {
      throw new ArithmeticException("Overflow: bitsMoldura muito grande para int");
    }
    return (int) (1L << bitsMoldura);
  }

  public long getTamanhoEspacoEnderecoVirtual() {
    return 1L << bitsEnderecoVirtual;
  }
}
