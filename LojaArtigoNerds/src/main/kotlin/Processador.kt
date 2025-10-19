package org.example

import ItemEstoque
import java.text.Normalizer

// --- Funções Auxiliares de Processamento ---

/**
 * Remove acentos de uma string.
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
private fun processarCampoOpcional(valor: String): String? {
    val valorProcessado = valor.uppercase().semAcentos()
    return if (valorProcessado == "-") null else valorProcessado
}

// --- Funções Principais de Parsing ---

/**
 * Processa a lista de linhas do CSV de compras.
 *
 * @param linhas Lista de strings, onde cada uma é uma linha do compras.csv.
 * @return Uma lista de Pares, contendo o objeto Produto e a quantidade comprada.
 */
fun processarCompras(linhas: List<String>): List<Pair<Produto, Int>> {
    // Pula a primeira linha (cabeçalho) com .drop(1)
    // Usa mapNotNull para processar cada linha e descartar
    // qualquer uma que falhe no parsing (retornando null)
    return linhas.drop(1).mapNotNull { linha ->
        try {
            // Tenta fazer o parse da linha
            parseLinhaCompra(linha)
        } catch (e: Exception) {
            // Se falhar (ex: Enum não encontrado, erro de .toInt()),
            // imprime um erro e retorna null para essa linha
            println("ERRO: Falha ao processar linha de compra: '$linha'. Causa: ${e.message}")
            null
        }
    }
}

/**
 * Converte uma única linha de String em um objeto Produto e sua quantidade.
 * Esta função é baseada na ordem das colunas do seu 'compras.csv'.
 */
private fun parseLinhaCompra(csvLine: String): Pair<Produto, Int> {
    // Regra: Transformar entradas para maiúsculo [cite: 94]
    val campos = csvLine.uppercase().split(",")

    // --- Colunas Comuns (Baseado no seu compras.csv) ---
    // [0]CATEGORIA, [1]NOME, [2]PRECO_COMPRA, [3]PRECO_VENDA, [4]CODIGO, [5]QUANTIDADE
    val categoria = campos[0]
    val nome = campos[1].semAcentos()
    val precoCompra = campos[2].toDouble()
    val precoVenda = campos[3].toDouble()
    val codigoOriginal = campos[4]
    val quantidade = campos[5].toInt()

    val produto: Produto = when (categoria) {
        "ROUPA" -> {
            // Colunas: [6]ATTR_1=Tipo, [7]ATTR_2=Tamanho, [8]ATTR_3=Cor1, [9]ATTR_4=Cor2
            Roupa().apply {
                this.nome = nome
                this.precoComprado = precoCompra
                this.precoVendido = precoVenda
                this.codigoOriginal = codigoOriginal
                this.codigoFormatado = "R-$codigoOriginal" // [cite: 38]
                // Atributos específicos
                this.tipo = Roupa.Tipo.valueOf(campos[6].semAcentos())
                this.tamanho = Roupa.Tamanho.valueOf(campos[7].semAcentos())
                this.corPrimaria = campos[8].semAcentos()
                this.corSecundaria = processarCampoOpcional(campos[9]) // Trata o "-"
            }
        }
        "ELETRONICO" -> {
            // Colunas: [6]ATTR_1=Tipo, [7]ATTR_2=Versao, [8]ATTR_3=Ano
            Eletronico().apply {
                this.nome = nome
                this.precoComprado = precoCompra
                this.precoVendido = precoVenda
                this.codigoOriginal = codigoOriginal
                this.codigoFormatado = "E-$codigoOriginal" // [cite: 38]
                // Atributos específicos
                this.tipo = Eletronico.Tipo.valueOf(campos[6].semAcentos())
                this.versao = processarCampoOpcional(campos[7]) ?: "" // Se for "-", vira null, e o elvis ?: "" garante que seja uma String não-nula, como a classe espera
                this.anoDeFabricacao = campos[8].toInt()
            }
        }
        "COLECIONAVEL" -> {
            // Colunas: [6]ATTR_1=Tipo, [7]ATTR_2=Material, [8]ATTR_3=Tamanho, [9]ATTR_4=Relevancia
            Colecionavel().apply {
                this.nome = nome
                this.precoComprado = precoCompra
                this.precoVendido = precoVenda
                this.codigoOriginal = codigoOriginal
                this.codigoFormatado = "C-$codigoOriginal" // [cite: 38]
                // Atributos específicos
                this.tipo = Colecionavel.Tipo.valueOf(campos[6].semAcentos())
                this.materialFabricacao = Colecionavel.MaterialFabricacao.valueOf(campos[7].semAcentos())
                this.tamanhoEmCentimetros = campos[8].toInt()
                this.relevancia = Colecionavel.Relevancia.valueOf(campos[9].semAcentos())
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
 *
 * @param compras Lista de Pares (Produto, Quantidade) vinda do 'compras.csv'.
 * @param linhasVendas Lista de Strings (linhas) vinda do 'vendas.csv'[cite: 48].
 * @return Um Mapa onde a chave é o 'codigoFormatado' (ex: "R-RGL-001")
 * e o valor é o objeto 'ItemEstoque' (que contém o Produto e a qtd final).
 */
fun consolidarEstoque(
    compras: List<Pair<Produto, Int>>,
    linhasVendas: List<String>
): Map<String, ItemEstoque> {

    val estoque = mutableMapOf<String, ItemEstoque>()

    // 1. Processar todas as Compras
    println("LOG: Processando ${compras.size} registros de compra...")
    for ((produto, quantidadeComprada) in compras) {
        val chave = produto.codigoFormatado

        val itemExistente = estoque[chave]

        if (itemExistente != null) {
            // Se o item já foi comprado antes, apenas soma a quantidade
            itemExistente.quantidade += quantidadeComprada
        } else {
            // Se for a primeira vez, cria o novo ItemEstoque
            estoque[chave] = ItemEstoque(produto, quantidadeComprada)
        }
    }
    println("LOG: Compras processadas. ${estoque.size} produtos únicos no estoque (antes das vendas).")


    // 2. Criar um mapa de lookup para processar vendas
    // O arquivo de vendas usa o CÓDIGO ORIGINAL (ex: "RGL-001") [cite: 48]
    // Nosso estoque usa o CÓDIGO FORMATADO (ex: "R-RGL-001")
    // Este mapa nos permite achar o "ItemEstoque" usando o código original.
    val lookupPorCodigoOriginal = estoque.values.associateBy { it.produto.codigoOriginal }

    // 3. Processar todas as Vendas
    println("LOG: Processando ${linhasVendas.size - 1} registros de venda...")
    for (linhaVenda in linhasVendas.drop(1)) { // Pula o cabeçalho
        try {
            val campos = linhaVenda.uppercase().split(",")
            val codigoOriginalVenda = campos[0]
            val quantidadeVendida = campos[1].toInt()

            // Encontra o item no nosso estoque
            val itemEmEstoque = lookupPorCodigoOriginal[codigoOriginalVenda]

            if (itemEmEstoque != null) {
                // Subtrai a quantidade vendida
                itemEmEstoque.quantidade -= quantidadeVendida
            } else {
                // Venda de um item que nunca foi comprado?
                println("AVISO: Venda registrada para o código '$codigoOriginalVenda', mas este item não existe no estoque de compras.")
            }
        } catch (e: Exception) {
            println("ERRO: Falha ao processar linha de venda: '$linhaVenda'. Causa: ${e.message}")
        }
    }
    println("LOG: Vendas processadas. Estoque consolidado finalizado.")

    // Retorna o mapa como imutável
    return estoque
}