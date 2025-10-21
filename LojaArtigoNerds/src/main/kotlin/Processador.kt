package org.example

import ItemEstoque
import java.text.Normalizer

/**
 * Remove acentos de uma string, conforme regra do projeto.
 */
private fun String.semAcentos(): String {
    val normalizado = Normalizer.normalize(this, Normalizer.Form.NFD)
    return normalizado.replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "")
}

/**
 * Processa um campo de texto que pode ser opcional.
 * Se o valor for "-", retorna null.
 * Também aplica as regras de maiúsculo e remoção de acentos.
 */
private fun processarCampoVazio(valor: String): String? {
    val valorProcessado = valor.uppercase().semAcentos()
    return if (valorProcessado == "-") null else valorProcessado
}

/**
 * Processa um campo numérico (Int) que pode ser "-".
 * Se for "-", retorna 0. Caso contrário, converte para Int.
 */
private fun processarCampoNumerico(valor: String): Int {
    // Remove espaços em branco que podem vir do CSV antes de checar
    return if (valor.trim() == "-") 0 else valor.trim().toInt()
}

/**
 * Processa a string para ser usada em um Enum.valueOf().
 * Remove acentos, coloca em maiúsculo e substitui hífens por underscores.
 */
private fun String.paraEnum(): String {
    return this.semAcentos().uppercase().replace("-", "_")
}

// Funções Principais de Parsing

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

    // [0]CODIGO, [1]QUANTIDADE, [2]NOME, [3]PRECO_COMPRA, [4]PRECO_VENDA, [5]CATEGORIA
    val codigoOriginal = campos[0]
    val quantidade = campos[1].toInt()
    val nome = campos[2].semAcentos()
    val precoCompra = campos[3].toDouble()
    val precoVenda = campos[4].toDouble()
    val categoria = campos[5].semAcentos()

    val produto: Produto = when (categoria) {
        "ROUPA" -> {
            // Colunas: [6]Tipo, [7]Tamanho, [8]Cor1, [9]Cor2
            Roupa().apply {
                this.nome = nome
                this.precoComprado = precoCompra
                this.precoVendido = precoVenda
                this.codigoOriginal = codigoOriginal
                this.codigoFormatado = "R-$codigoOriginal"

                // Atributos específicos
                this.tipo = Roupa.Tipo.valueOf(campos[6].paraEnum())
                this.tamanho = Roupa.Tamanho.valueOf(campos[7].paraEnum())
                this.corPrimaria = campos[8].semAcentos()
                this.corSecundaria = processarCampoVazio(campos[9])
            }
        }
        "ELETRONICO" -> {
            // Colunas: [6]Tipo, [10]Versao, [11]Ano
            Eletronico().apply {
                this.nome = nome
                this.precoComprado = precoCompra
                this.precoVendido = precoVenda
                this.codigoOriginal = codigoOriginal
                this.codigoFormatado = "E-$codigoOriginal"

                // Atributos específicos
                this.tipo = Eletronico.Tipo.valueOf(campos[6].paraEnum())
                this.versao = processarCampoVazio(campos[10]) ?: ""
                this.anoDeFabricacao = processarCampoNumerico(campos[11])
            }
        }
        "COLECIONAVEL" -> {
            // Colunas: [6]Tipo, [7]Tamanho, [12]Material, [13]Relevancia
            Colecionavel().apply {
                this.nome = nome
                this.precoComprado = precoCompra
                this.precoVendido = precoVenda
                this.codigoOriginal = codigoOriginal
                this.codigoFormatado = "C-$codigoOriginal"

                // Atributos específicos
                this.tipo = Colecionavel.Tipo.valueOf(campos[6].paraEnum())
                this.materialFabricacao = Colecionavel.MaterialFabricacao.valueOf(campos[12].paraEnum())
                this.tamanhoEmCentimetros = processarCampoNumerico(campos[7])
                this.relevancia = Colecionavel.Relevancia.valueOf(campos[13].paraEnum())
            }
        }
        else -> throw IllegalArgumentException("Categoria desconhecida: $categoria")
    }

    // Retorna o produto criado e a quantidade lida
    return Pair(produto, quantidade)
}

/**
 * Cria o estoque consolidado processando a lista de compras e depois
 * aplicando (subtraindo) a lista de vendas.
 */
fun consolidarEstoque(
    compras: List<Pair<Produto, Int>>,
    linhasVendas: List<String>
): Map<String, ItemEstoque> {

    val estoque = mutableMapOf<String, ItemEstoque>()

    // Processar todas as Compras
    logDebug("LOG: Processando ${compras.size} registros de compra...")
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

    // Processar todas as Vendas
    logDebug("LOG: Processando ${linhasVendas.size - 1} registros de venda...")
    for (linhaVenda in linhasVendas.drop(1)) { // Pula o cabeçalho
        try {
            val campos = linhaVenda.uppercase().split(",")

            val codigoFormatadoVenda = campos[0]
            val quantidadeVendida = campos[1].toInt()

            // Procura o item diretamente no mapa de estoque principal
            val itemEmEstoque = estoque[codigoFormatadoVenda]

            if (itemEmEstoque != null) {
                // Se encontrou, subtrai a quantidade
                itemEmEstoque.quantidade -= quantidadeVendida
            } else {
                // Se não encontrou, dispara o aviso (que agora será para itens que realmente não existem)
                logDebug("AVISO: Venda registrada para o código '$codigoFormatadoVenda', mas este item não existe no estoque de compras.")
            }
        } catch (e: Exception) {
            logDebug("ERRO: Falha ao processar linha de venda: '$linhaVenda'. Causa: ${e.message}")
        }
    }

    logDebug("LOG: Vendas processadas. Estoque consolidado finalizado.")

    return estoque
}