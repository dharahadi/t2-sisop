package config;

import model.TipoSegmento;

/**
 * Responsável por particionar o espaço de endereços virtuais em segmentos e
 * responder rapidamente a qual segmento um endereço pertence.
 */
public class LayoutSegmentos {

  private final long inicioText;
  private final long fimText;

  private final long inicioData;
  private final long fimData;

  private final long inicioBss;
  private final long fimBss;

  private final long inicioStack;
  private final long fimStack;

  /**
   * Pré-calcula os intervalos de cada segmento a partir da configuração atual.
   */
  public LayoutSegmentos(Configuracao config) {
    long tamanhoEV = config.getTamanhoEspacoEnderecoVirtual();

    // Segmento .text começa em 0 e ocupa o tamanho definido na configuração.
    this.inicioText = 0;
    this.fimText = inicioText + config.getTamanhoText() - 1;

    // Segmento .data vem logo após o .text.
    this.inicioData = fimText + 1;
    this.fimData = inicioData + config.getTamanhoData() - 1;

    // .bss ocupa o intervalo seguinte, com tamanho calculado dinamicamente.
    long tamanhoBss = config.getTamanhoBss();
    this.inicioBss = fimData + 1;
    this.fimBss = inicioBss + tamanhoBss - 1;

    // A pilha cresce de cima para baixo: reservamos o final do espaço virtual.
    this.fimStack = tamanhoEV - 1;
    this.inicioStack = fimStack - config.getTamanhoStack() + 1;
  }

  /**
   * Classifica um endereço virtual retornando o segmento correspondente.
   */
  public TipoSegmento getSegmento(long enderecoVirtual) {
    if (enderecoVirtual >= inicioText && enderecoVirtual <= fimText) {
      return TipoSegmento.TEXT;
    }
    if (enderecoVirtual >= inicioData && enderecoVirtual <= fimData) {
      return TipoSegmento.DATA;
    }
    if (enderecoVirtual >= inicioBss && enderecoVirtual <= fimBss) {
      return TipoSegmento.BSS;
    }
    if (enderecoVirtual >= inicioStack && enderecoVirtual <= fimStack) {
      return TipoSegmento.STACK;
    }
    return TipoSegmento.DESCONHECIDO;
  }
}
