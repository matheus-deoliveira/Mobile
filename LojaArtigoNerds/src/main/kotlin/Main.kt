import org.example.consolidarEstoque
import org.example.logDebug
import org.example.processarCompras
import java.io.File
import kotlin.system.exitProcess

// O mapa do estoque consolidado.
// Declarado aqui para ser acessível por outras funções (balancete, busca, etc.)
private lateinit var estoque: Map<String, ItemEstoque>

fun main (args: Array<String>){

    // Para depuração, vamos imprimir o diretório de trabalho atual
    logDebug("Diretório de Trabalho Atual: ${System.getProperty("user.dir")}")

    val (pastaEntrada, pastaSaida) = validarEObterPastas(args)

    val caminhoCompras = "$pastaEntrada/compras.csv"
    val caminhoVendas = "$pastaEntrada/vendas.csv"

    // Processamento de Compras
    logDebug("\nLendo arquivo de compras em: $caminhoCompras")
    val linhasCompras = lerArquivoCsv (caminhoCompras)
    var produtosComprados: List<Pair<org.example.Produto, Int>> = emptyList()

    if (linhasCompras.isNotEmpty()) {
        logDebug("-> Sucesso! ${linhasCompras.size - 1} registros de compras encontrados. Processando...")
        produtosComprados = processarCompras(linhasCompras)
    } else {
        logDebug("-> Arquivo de compras vazio ou não encontrado.")
    }

    // Processamento de Vendas
    logDebug("\nLendo arquivo de vendas em: $caminhoVendas")
    val linhasVendas : List<String> = lerArquivoCsv (caminhoVendas)
    if (linhasVendas.isNotEmpty()) {
        logDebug("-> Sucesso! ${linhasVendas.size - 1} registros de vendas encontrados.")
    }

    // Gerenciamento de Compra e Venda
    logDebug("\nConsolidando estoque a partir de compras e vendas...")
    estoque = consolidarEstoque(produtosComprados, linhasVendas)

    estoque.forEach { (codigo, item) ->
        logDebug("   - [$codigo] ${item.produto.nome}: ${item.quantidade} unidades")
    }

    // Gerenciamento de Estoque
    salvarEstoqueConsolidado(estoque, pastaSaida)

    // TODO: Balancete da Loja
    // TODO: Sistema de Busca
}

/**
 * Valida os argumentos de entrada e retorna os caminhos.
 * Se inválido, termina o programa.
 */
private fun validarEObterPastas(args: Array<String>): Pair<String, String> {
    if (args.size < 2) {
        println("ERRO: Argumentos insuficientes.")
        exitProcess(1)
    }
    return Pair(args[0], args[1]) // Retorna a pasta de entrada e saída
}

/**
 * Lê um arquivo (como um CSV) a partir do caminho completo e retorna seu conteúdo
 * como uma lista de strings, onde cada string é uma linha do arquivo.
 */
private fun lerArquivoCsv(caminhoDoArquivo: String): List<String> {
    val arquivo = File(caminhoDoArquivo)

    // Verifica se o arquivo existe e é um arquivo (não um diretório)
    if (!arquivo.exists() || !arquivo.isFile) {
        println("ERRO: Arquivo não encontrado ou é um diretório: $caminhoDoArquivo")
        return emptyList()
    }

    return try {
        arquivo.readLines()
    } catch (e: SecurityException) {
        println("ERRO: Sem permissão para ler o arquivo: ${e.message}")
        emptyList()
    } catch (e: Exception) {
        println("ERRO: Ocorreu um erro inesperado ao ler o arquivo: ${e.message}")
        emptyList()
    }
}

/**
 * Escreve um conteúdo de string em um arquivo no caminho especificado.
 * Cria o arquivo se não existir, ou sobrescreve se existir.
 */
private fun escreverArquivo(caminhoDoArquivo: String, conteudo: String){
    val arquivo = File(caminhoDoArquivo)

    try {
        arquivo.parentFile.mkdirs()
    } catch (e: SecurityException) {
        println("ERRO: Sem permissão para criar diretórios para: $caminhoDoArquivo")
        return
    }

    try {
        arquivo.writeText(conteudo)
    } catch (e: SecurityException) {
        println("ERRO: Sem permissão para escrever o arquivo: ${e.message}")
    } catch (e: Exception) {
        println("ERRO: Ocorreu um erro inesperado ao escrever o arquivo: ${e.message}")
    }
}

/**
 * Pega o mapa de estoque consolidado e o salva em um arquivo CSV
 * no formato: codigo,nome,quantidade
 */
private fun salvarEstoqueConsolidado(estoque: Map<String, ItemEstoque>, pastaSaida: String) {
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