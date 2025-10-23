import domain.Colecionavel
import domain.Eletronico
import domain.Produto
import domain.Roupa
import utils.logDebug
import utils.paraEnum
import utils.processarCampoNumerico
import utils.processarCampoOpcional
import utils.semAcentos

class ProcessadorCompras {
    /**
     * Processa a lista de linhas do CSV de compras.
     */
    fun processarCompras(linhas: List<String>): List<Pair<Produto, Int>> {
        return linhas.drop(1).mapNotNull { linha ->
            try {
                parseLinhaCompra(linha)
            } catch (e: Exception) {
                logDebug("ERRO: Falha ao processar linha de compra: '$linha'. Causa: ${e.message}")
                null
            }
        }
    }

    /**
     * Converte uma única linha de String em um objeto Produto e sua quantidade.
     */
    private fun parseLinhaCompra(csvLine: String): Pair<Produto, Int> {
        // Regra: Transformar entradas para maiúsculo
        val campos = csvLine.uppercase().split(",")

        val codigoOriginal = campos[0]
        val quantidade = campos[1].toInt()
        val nome = campos[2].semAcentos()
        val precoCompra = campos[3].toDouble()
        val precoVenda = campos[4].toDouble()
        val categoria = campos[5].semAcentos()

        val produto: Produto = when (categoria) {
            "ROUPA" -> {
                Roupa().apply {
                    this.nome = nome
                    this.precoComprado = precoCompra
                    this.precoVendido = precoVenda
                    this.codigoOriginal = codigoOriginal
                    this.codigoFormatado = "R-$codigoOriginal"
                    this.tipo = Roupa.Tipo.valueOf(campos[6].paraEnum())
                    this.tamanho = Roupa.Tamanho.valueOf(campos[7].paraEnum())
                    this.corPrimaria = campos[8].semAcentos()
                    this.corSecundaria = processarCampoOpcional(campos[9])
                }
            }
            "ELETRONICO" -> {
                Eletronico().apply {
                    this.nome = nome
                    this.precoComprado = precoCompra
                    this.precoVendido = precoVenda
                    this.codigoOriginal = codigoOriginal
                    this.codigoFormatado = "E-$codigoOriginal"
                    this.tipo = Eletronico.Tipo.valueOf(campos[6].paraEnum()) // <-- Usa Utils.kt
                    this.versao = processarCampoOpcional(campos[10]) ?: "" // <-- Usa Utils.kt
                    this.anoDeFabricacao = processarCampoNumerico(campos[11]) // <-- Usa Utils.kt
                }
            }
            "COLECIONAVEL" -> {
                Colecionavel().apply {
                    this.nome = nome
                    this.precoComprado = precoCompra
                    this.precoVendido = precoVenda
                    this.codigoOriginal = codigoOriginal
                    this.codigoFormatado = "C-$codigoOriginal"
                    this.tipo = Colecionavel.Tipo.valueOf(campos[6].paraEnum()) // <-- Usa Utils.kt
                    this.materialFabricacao = Colecionavel.MaterialFabricacao.valueOf(campos[12].paraEnum()) // <-- Usa Utils.kt
                    this.tamanhoEmCentimetros = processarCampoNumerico(campos[7]) // <-- Usa Utils.kt
                    this.relevancia = Colecionavel.Relevancia.valueOf(campos[13].paraEnum()) // <-- Usa Utils.kt
                }
            }
            else -> throw IllegalArgumentException("Categoria desconhecida: $categoria")
        }

        return Pair(produto, quantidade)
    }
}