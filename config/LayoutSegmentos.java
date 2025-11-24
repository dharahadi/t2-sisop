package config;

import model.TipoSegmento;

public class LayoutSegmentos {
  private final Configuracao config;

  private final long inicioText;
  private final long fimText;

  private final long inicioData;
  private final long fimData;

  private final long inicioBss;
  private final long fimBss;

  private final long inicioStack;
  private final long fimStack;

  public LayoutSegmentos(Configuracao config) {
    this.config = config;

    long tamanhoEV = config.getTamanhoEspacoEnderecoVirtual();

    this.inicioText = 0;
    this.fimText = inicioText + config.getTamanhoText() - 1;

    this.inicioData = fimText + 1;
    this.fimData = inicioData + config.getTamanhoData() - 1;

    long tamanhoBss = config.getTamanhoBss();
    this.inicioBss = fimData + 1;
    this.fimBss = inicioBss + tamanhoBss - 1;

    this.fimStack = tamanhoEV - 1;
    this.inicioStack = fimStack - config.getTamanhoStack() + 1;
  }

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
