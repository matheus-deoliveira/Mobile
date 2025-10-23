import domain.Produto
import org.example.logDebug
import java.io.File
import java.io.IOException
import java.util.Locale

class Balancete {
    fun balancete(
        produtosComprados: List<Pair<Produto, Int>>,
        produtosVendidos: List<Pair<String, Int>>,
        pastaSaida: String
    )
    {
        logDebug("\nGerando relatório de balancete")

        try {
            // Calcular o total de compras
            val totalCompras = produtosComprados.sumOf { (produto, quantidade) ->
                produto.precoComprado * quantidade
            }

            // Para calcular as vendas, precisamos de um mapa de preços de venda
            val catalogoPrecosVenda = produtosComprados
                .map { it.first }
                .distinctBy { it.codigoFormatado }
                .associate { it.codigoFormatado to it.precoVendido }

            // Calcula o total das vendas
            val totalVendas = produtosVendidos.sumOf { (nomeProduto, quantidade) ->
                val precoVenda = catalogoPrecosVenda.getOrDefault(nomeProduto, 0.0)

                if (precoVenda == 0.0) {
                    logDebug("AVISO: Produto vendido '$nomeProduto' não possui preço de venda no catálogo. Venda não será contabilizada.")
                }

                precoVenda * quantidade
            }

            // Calcular o balancete
            val totalBalancete = totalVendas - totalCompras

            // Formatar os números para o CSV
            val fTotalCompras = String.format(Locale.US, "%.1f", totalCompras)
            val fTotalVendas = String.format(Locale.US, "%.1f", totalVendas)
            val fTotalBalancete = String.format(Locale.US, "%.1f", totalBalancete)

            val linhasCsv = listOf(
                "COMPRAS,$fTotalCompras",
                "VENDAS,$fTotalVendas",
                "BALANCETE,$fTotalBalancete"
            )
            val conteudoCsv = linhasCsv.joinToString("\n")

            // Garantir que o diretório de saída exista
            val diretorio = File(pastaSaida)
            if (!diretorio.exists()) {
                diretorio.mkdirs()
            }

            // Escrever o conteúdo no arquivo
            val arquivoFinal = File(diretorio, "balancete.csv")
            arquivoFinal.writeText(conteudoCsv, Charsets.UTF_8)

            logDebug("Relatório 'balancete.csv' gerado com sucesso em: ${arquivoFinal.absolutePath}")

        } catch (e: IOException) {
            logDebug("Erro de E/S ao escrever o arquivo: ${e.message}")
        } catch (e: Exception) {
            logDebug("Erro inesperado ao gerar balancete: ${e.message}")
        }
    }
}