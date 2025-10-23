import domain.Colecionavel
import domain.Eletronico
import domain.ItemEstoque
import domain.Produto
import domain.Roupa
import org.example.logDebug

class GerenciadorEstoque {
    /**
     * Cria o estoque geral processando a lista de compras e depois
     * aplicando a lista de vendas.
     *
     * @param compras List<Pair<(Produto, Quantidade Comprada)>>
     * @param vendas List<Pair<(Código Formatado, Quantidade Vendida)>>
     * @return Map<Código Formatado, ItemEstoque>
     */
    fun consolidarEstoque(
        compras: List<Pair<Produto, Int>>,
        vendas: List<Pair<String, Int>>
    ): Map<String, ItemEstoque> {

        val estoque = mutableMapOf<String, ItemEstoque>()

        // Processar todas as compras
        logDebug("Processando ${compras.size} registros de compra para consolidar...")
        for ((produto, quantidadeComprada) in compras) {
            val chave = produto.codigoFormatado
            val itemExistente = estoque[chave]

            if (itemExistente != null) {
                itemExistente.quantidade += quantidadeComprada
            } else {
                estoque[chave] = ItemEstoque(produto, quantidadeComprada)
            }
        }
        logDebug("Compras processadas. ${estoque.size} produtos únicos no estoque (antes das vendas).")

        // Processar todas as vendas
        logDebug("Aplicando ${vendas.size} registros de venda ao estoque...")
        for ((codigoFormatadoVenda, quantidadeVendida) in vendas) {

            val itemEmEstoque = estoque[codigoFormatadoVenda]

            if (itemEmEstoque != null) {
                // Se encontrou, subtrai a quantidade
                itemEmEstoque.quantidade -= quantidadeVendida
            } else {
                // Se não encontrou, dispara o aviso
                logDebug("Venda registrada para o código '$codigoFormatadoVenda', mas este item não existe no estoque de compras.")
            }
        }

        logDebug("Vendas aplicadas. Estoque consolidado finalizado.")

        return estoque
    }

    /**
     * Pega o mapa de estoque geral e o salva em um arquivo CSV
     * no formato: codigo,nome,quantidade
     */
    fun salvarEstoqueGeral(estoque: Map<String, ItemEstoque>, pastaSaida: String) {
        logDebug("\nGerando arquivo de estoque geral")

        // Define o cabeçalho do CSV
        val header = "codigo,nome,quantidade"

        // Mapeia cada entrada do mapa para uma linha do CSV
        val linhasCsv = estoque.map { (codigo, item) ->
            "${codigo},${item.produto.nome},${item.quantidade}"
        }

        // Junta o cabeçalho e as linhas com quebras de linha
        val conteudoCompleto = (listOf(header) + linhasCsv).joinToString("\n")

        // Define o caminho do arquivo de saída e salva
        val caminhoArquivoSaida = "$pastaSaida/estoque_geral.csv"
        escreverArquivo(caminhoArquivoSaida, conteudoCompleto)

        logDebug("Estoque salvo salvo com sucesso em: $caminhoArquivoSaida")
    }

    fun salvarEstoquePorCategoria(
        estoque: Map<String, ItemEstoque>,
        compras: List<Pair<Produto, Int>>,
        pastaSaida: String
    ) {
        logDebug("\nGerando arquivo de estoque por categoria")

        val header = "CATEGORIA,QUANTIDADE"

        // Usando um LinkedHashSet para manter a ordem de inserção
        val ordemCategorias = LinkedHashSet<String>()

        // Iterando na lista de compras original para pegar a ordem
        for ((produto, _) in compras) {
            when (produto) {
                is Roupa -> ordemCategorias.add("ROUPA")
                is Colecionavel -> ordemCategorias.add("COLECIONAVEL")
                is Eletronico -> ordemCategorias.add("ELETRONICO")
            }
        }

        logDebug("Ordem das categorias determinada: $ordemCategorias")

        val totaisPorCategoria = mutableMapOf(
            "ROUPA" to 0,
            "COLECIONAVEL" to 0,
            "ELETRONICO" to 0
        )

        // Itera no estoque final para somar as quantidades
        for (item in estoque.values) {
            when (item.produto) { //
                is Colecionavel -> totaisPorCategoria["COLECIONAVEL"] = totaisPorCategoria["COLECIONAVEL"]!! + item.quantidade //
                is Roupa -> totaisPorCategoria["ROUPA"] = totaisPorCategoria["ROUPA"]!! + item.quantidade //
                is Eletronico -> totaisPorCategoria["ELETRONICO"] = totaisPorCategoria["ELETRONICO"]!! + item.quantidade //
                else -> throw IllegalArgumentException("Produto desconhecido: ${item.produto}")
            }
        }
        logDebug("Totais calculados: $totaisPorCategoria")

        val linhasCsv = mutableListOf<String>()

        // Itera na lista ordenada de categorias
        for (categoria in ordemCategorias) {
            val quantidade = totaisPorCategoria[categoria] ?: 0
            linhasCsv.add("$categoria,$quantidade")
        }

        val conteudoCompleto = (listOf(header) + linhasCsv).joinToString("\n")
        val caminhoArquivoSaida = "$pastaSaida/estoque_categorias.csv"

        escreverArquivo(caminhoArquivoSaida, conteudoCompleto)

        logDebug("Estoque salvo salvo com sucesso em: $caminhoArquivoSaida")
    }
}