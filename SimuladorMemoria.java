import config.Configuracao;
import config.LayoutSegmentos;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import memory.MemoriaFisica;
import mmu.TLB;
import mmu.TabelaPaginas;
import model.TipoSegmento;

/**
 * Ponto de entrada do simulador. Faz a leitura das configurações, processa cada
 * endereço virtual e gera um relatório detalhado com o comportamento da TLB,
 * tabela de páginas e memória física.
 */
public class SimuladorMemoria {
  public static void main(String[] args) throws Exception {
    Configuracao config;

    // Tenta carregar configuração de argumentos ou arquivo
    if (args.length > 0) {
      if (args.length == 1 && args[0].endsWith(".properties")) {
        // Arquivo de configuração
        config = Configuracao.deArquivo(args[0]);
      } else {
        System.err.println("Uso: java SimuladorMemoria [config.properties]");
        System.err.println(
            "   ou: java SimuladorMemoria <bitsVirtual> <bitsFisico> <bitsPagina> <bitsTLB> <niveis> <bitsText> <bitsData> <bitsStack> [arquivoEntrada] [arquivoSaida]");
        config = Configuracao.configuracaoPadrao();
      }
    } else {
      config = Configuracao.configuracaoPadrao();
    }

    // Componentes principais da simulação.
    LayoutSegmentos layout = new LayoutSegmentos(config);
    TLB tlb = new TLB(config.getEntradasTLB());
    TabelaPaginas tabela = new TabelaPaginas(config);
    MemoriaFisica memoria = new MemoriaFisica(config.getNumeroMoldurasFisicas());

    long instante = 0;

    try (BufferedReader br = new BufferedReader(new FileReader(config.getArquivoEntrada()));
        PrintWriter out = new PrintWriter(new FileWriter(config.getArquivoSaida()))) {

      String linha;
      int tamanhoPagina = config.getTamanhoPagina();
      int bitsDeslocamento = config.getBitsDeslocamentoPagina();

      while ((linha = br.readLine()) != null) {
        linha = linha.trim();
        if (linha.isEmpty()) {
          continue;
        }

        long enderecoVirtual = Long.parseLong(linha);
        instante++;

        // Separa VPN e deslocamento conforme a configuração.
        int paginaVirtual = (int) (enderecoVirtual >> bitsDeslocamento);
        int deslocamento = (int) (enderecoVirtual & (tamanhoPagina - 1));

        TipoSegmento segmento = layout.getSegmento(enderecoVirtual);

        boolean tlbHit = false;
        int moldura = tlb.lookup(paginaVirtual, instante);

        if (moldura != -1) {
          tlbHit = true;
          memoria.atualizaAcesso(moldura, instante);
        } else {
          moldura = tabela.obtemMoldura(paginaVirtual);
          if (moldura == -1) {
            int molduraSelecionada = memoria.selecionaMolduraParaUso(instante);

            int paginaAntiga = tabela.encontraPaginaPorMoldura(molduraSelecionada);
            if (paginaAntiga != -1) {
              // Desmapeia página antiga e invalida entrada na TLB
              tabela.desmapeiaPagina(paginaAntiga);
              tlb.invalidaEntrada(paginaAntiga);
            }

            tabela.mapeiaPagina(paginaVirtual, molduraSelecionada);
            moldura = molduraSelecionada;

            // Armazena endereço virtual completo (início da página)
            long enderecoInicioPagina = (long) paginaVirtual << bitsDeslocamento;
            memoria.setaConteudo(moldura, enderecoInicioPagina, instante);
          } else {
            memoria.atualizaAcesso(moldura, instante);
          }

          tlb.insereOuAtualiza(paginaVirtual, moldura, instante);
        }

        long enderecoFisico = ((long) moldura << bitsDeslocamento) | (long) deslocamento;

        out.printf("VA=%d VPN=%d OFFSET=%d SEG=%s FRAME=%d PA=%d TLB_HIT=%b%n",
            enderecoVirtual, paginaVirtual, deslocamento, segmento,
            moldura, enderecoFisico, tlbHit);
      }

      out.println();
      out.println("=== TABELA DE PAGINAS ===");
      tabela.imprimirTabela(out);

      out.println();
      out.println("=== MEMORIA FISICA (MOLDURAS) ===");
      long[] conteudo = memoria.getConteudoMolduras();
      for (int i = 0; i < conteudo.length; i++) {
        out.printf("FRAME=%d CONTENT=%d%n", i, conteudo[i]);
      }
    }
  }
}
