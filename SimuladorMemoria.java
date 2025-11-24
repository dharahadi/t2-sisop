import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;

import config.Configuracao;
import config.LayoutSegmentos;
import memory.MemoriaFisica;
import model.TipoSegmento;
import mmu.TLB;
import mmu.TabelaPaginas;

public class SimuladorMemoria {
  public static void main(String[] args) throws Exception {
    Configuracao config = Configuracao.configuracaoPadrao();
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

        int paginaVirtual = (int) (enderecoVirtual >> bitsDeslocamento);
        int deslocamento = (int) (enderecoVirtual & (tamanhoPagina - 1));

        TipoSegmento segmento = layout.getSegmento(enderecoVirtual);

        boolean tlbHit = false;
        int moldura = tlb.lookup(paginaVirtual);

        if (moldura != -1) {
          tlbHit = true;
          memoria.atualizaAcesso(moldura, instante);
        } else {
          moldura = tabela.obtemMoldura(paginaVirtual);
          if (moldura == -1) {
            int molduraSelecionada = memoria.selecionaMolduraParaUso(instante);

            int paginaAntiga = tabela.encontraPaginaPorMoldura(molduraSelecionada);
            if (paginaAntiga != -1) {
              tabela.desmapeiaPagina(paginaAntiga);
            }

            tabela.mapeiaPagina(paginaVirtual, molduraSelecionada);
            moldura = molduraSelecionada;

            memoria.setaConteudo(moldura, paginaVirtual, instante);
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
      int[] conteudo = memoria.getConteudoMolduras();
      for (int i = 0; i < conteudo.length; i++) {
        out.printf("FRAME=%d CONTENT=%d%n", i, conteudo[i]);
      }
    }
  }
}
