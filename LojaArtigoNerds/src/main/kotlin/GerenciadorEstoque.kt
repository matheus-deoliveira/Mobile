import domain.ItemEstoque
import domain.Produto
import org.example.logDebug

class GerenciadorEstoque {
    /**
     * Cria o estoque consolidado processando a lista de compras e depois
     * aplicando (subtraindo) a lista de vendas.
     *
     * @param compras Lista de Pares (Produto, Quantidade Comprada)
     * @param vendas Lista de Pares (Código Formatado, Quantidade Vendida)
     * @return Um mapa com o estoque final [Código Formatado -> ItemEstoque]
     */
    fun consolidarEstoque(
        compras: List<Pair<Produto, Int>>,
        vendas: List<Pair<String, Int>>
    ): Map<String, ItemEstoque> {

        val estoque = mutableMapOf<String, ItemEstoque>()

        // Processar todas as Compras
        logDebug("LOG: Processando ${compras.size} registros de compra para consolidar...")
        for ((produto, quantidadeComprada) in compras) {
            val chave = produto.codigoFormatado
            val itemExistente = estoque[chave]

            if (itemExistente != null) {
                itemExistente.quantidade += quantidadeComprada
            } else {
                estoque[chave] = ItemEstoque(produto, quantidadeComprada)
            }
        }
        logDebug("LOG: Compras processadas. ${estoque.size} produtos únicos no estoque (antes das vendas).")

        // 2. Processar todas as Vendas (já pré-processadas)
        logDebug("LOG: Aplicando ${vendas.size} registros de venda ao estoque...")
        for ((codigoFormatadoVenda, quantidadeVendida) in vendas) {

            val itemEmEstoque = estoque[codigoFormatadoVenda]

            if (itemEmEstoque != null) {
                // Se encontrou, subtrai a quantidade
                itemEmEstoque.quantidade -= quantidadeVendida
            } else {
                // Se não encontrou, dispara o aviso
                logDebug("AVISO: Venda registrada para o código '$codigoFormatadoVenda', mas este item não existe no estoque de compras.")
            }
        }

        logDebug("LOG: Vendas aplicadas. Estoque consolidado finalizado.")

        return estoque
    }

    /**
     * Pega o mapa de estoque consolidado e o salva em um arquivo CSV
     * no formato: codigo,nome,quantidade
     */
    fun salvarEstoqueConsolidado(estoque: Map<String, ItemEstoque>, pastaSaida: String) {
        logDebug("\nGerando arquivo de estoque consolidado...")

        // Define o cabeçalho do CSV
        val header = "codigo,nome,quantidade"

        // Mapeia cada entrada do mapa para uma linha do CSV
        // Sobre o "código modificado", estou assumindo que é o código-chave do mapa.
        val linhasCsv = estoque.map { (codigo, item) ->
            "${codigo},${item.produto.nome},${item.quantidade}"
        }

        // 3. Junta o cabeçalho e as linhas com quebras de linha
        val conteudoCompleto = (listOf(header) + linhasCsv).joinToString("\n")

        // 4. Define o caminho do arquivo de saída e salva
        val caminhoArquivoSaida = "$pastaSaida/estoque_geral.csv"
        escreverArquivo(caminhoArquivoSaida, conteudoCompleto)

        logDebug("-> Estoque consolidado salvo com sucesso em: $caminhoArquivoSaida")
    }
}