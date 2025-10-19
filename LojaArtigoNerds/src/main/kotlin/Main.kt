import org.example.consolidarEstoque
import org.example.processarCompras
import java.io.File
import kotlin.system.exitProcess

/**
 * Constante global para controlar os logs de depuração.
 * Mude para 'false' para desativar todas as mensagens de depuração no terminal.
 */
private const val MODO_DEBUG = true

// O mapa do estoque consolidado.
// Declarado aqui para ser acessível por outras funções (balancete, busca, etc.)
private lateinit var estoque: Map<String, ItemEstoque>

fun main (args: Array<String>){

    // Para depuração, vamos imprimir o diretório de trabalho atual
    logDebug("Diretório de Trabalho Atual: ${System.getProperty("user.dir")}")

    val (pastaEntrada, pastaSaida) = validarEObterPastas(args)

    val caminhoCompras = "$pastaEntrada/compras.csv"
    val caminhoVendas = "$pastaEntrada/vendas.csv"

    // --- Processamento de Compras ---
    logDebug("\nLendo arquivo de compras em: $caminhoCompras")
    val linhasCompras = lerArquivoCsv (caminhoCompras)
    var produtosComprados: List<Pair<org.example.Produto, Int>> = emptyList()

    if (linhasCompras.isNotEmpty()) {
        logDebug("-> Sucesso! ${linhasCompras.size - 1} registros de compras encontrados. Processando...")
        produtosComprados = processarCompras(linhasCompras)
    } else {
        logDebug("-> Arquivo de compras vazio ou não encontrado.")
    }

    // --- Processamento de Vendas ---
    logDebug("\nLendo arquivo de vendas em: $caminhoVendas")
    val linhasVendas : List<String> = lerArquivoCsv (caminhoVendas)
    if (linhasVendas.isNotEmpty()) {
        logDebug("-> Sucesso! ${linhasVendas.size - 1} registros de vendas encontrados.")
    }

    // 1. Gerenciamento de Compra e Venda
    logDebug("\nConsolidando estoque a partir de compras e vendas...")
    estoque = consolidarEstoque(produtosComprados, linhasVendas)

    estoque.forEach { (codigo, item) ->
        logDebug("   - [$codigo] ${item.produto.nome}: ${item.quantidade} unidades")
    }
    logDebug("-----------------------------------\n")
    // --- FIM DA MODIFICAÇÃO ---


    // TODO: 2. Gerenciamento de Estoque (Geração de arquivos)
    // TODO: 3. Balancete da Loja
    // TODO: 4. Sistema de Busca (Opcional)
}

/**
 * Imprime uma mensagem no console somente se o MODO_DEBUG estiver ativado.
 */
private fun logDebug(mensagem: String) {
    if (MODO_DEBUG) {
        println(mensagem)
    }
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