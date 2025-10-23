import utils.logDebug

class ProcessadorVendas {

    /**
     * Processa as linhas do CSV de vendas.
     * Retorna uma lista de Pares, onde cada Par contém o
     * <Código Formatado, Quantidade Vendida>.
     */
    fun processarVendas(linhas: List<String>): List<Pair<String, Int>> {

        logDebug("LOG: Processando ${linhas.size - 1} registros de venda...")

        return linhas.drop(1).mapNotNull { linhaVenda ->
            try {
                val campos = linhaVenda.uppercase().split(",")
                val codigoFormatadoVenda = campos[0]
                val quantidadeVendida = campos[1].toInt()

                // Validação simples
                if (codigoFormatadoVenda.isBlank()) {
                    throw IllegalArgumentException("Código do produto não pode ser vazio.")
                }
                if (quantidadeVendida <= 0) {
                    throw IllegalArgumentException("Quantidade vendida deve ser positiva.")
                }

                Pair(codigoFormatadoVenda, quantidadeVendida)

            } catch (e: Exception) {
                logDebug("ERRO: Falha ao processar linha de venda: '$linhaVenda'. Causa: ${e.message}")
                null
            }
        }
    }
}