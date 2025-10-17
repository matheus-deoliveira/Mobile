import java.io.File
import kotlin.system.exitProcess

/**
 * Constante global para controlar os logs de depuração.
 * Mude para 'false' para desativar todas as mensagens de depuração no terminal.
 */
private const val MODO_DEBUG = true

fun main (args: Array<String>){

    // Para depuração, vamos imprimir o diretório de trabalho atual
    logDebug("Diretório de Trabalho Atual: ${System.getProperty("user.dir")}")

    val (pastaEntrada, pastaSaida) = validarEObterPastas(args)

    val caminhoCompras = "$pastaEntrada/compras.csv"
    val caminhoVendas = "$pastaEntrada/vendas.csv"

    logDebug("\nLendo arquivo de compras em: $caminhoCompras")
    val compras = lerArquivoCsv (caminhoCompras)
    if (compras.isNotEmpty()) {
        logDebug("-> Sucesso! ${compras.size - 1} registros de compras encontrados.")
    }

    // TODO: Implementar processamento das compras

    logDebug("\nLendo arquivo de vendas em: $caminhoVendas")
    val vendas : List<String> = lerArquivoCsv (caminhoVendas)
    if (vendas.isNotEmpty()) {
        logDebug("-> Sucesso! ${vendas.size - 1} registros de vendas encontrados.")
    }

    // TODO: Implementar processamento das vendas
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
 *
 * @param caminhoDoArquivo O caminho completo para o arquivo.
 * @return Uma lista de strings (List<String>) com as linhas do arquivo.
 * Retorna uma lista vazia (emptyList) se o arquivo não for encontrado
 * ou se ocorrer um erro de leitura.
 */
private fun lerArquivoCsv(caminhoDoArquivo: String): List<String> {
    val arquivo = File(caminhoDoArquivo)

    // Verifica se o arquivo existe e é um arquivo (não um diretório)
    if (!arquivo.exists() || !arquivo.isFile) {
        println("ERRO: Arquivo não encontrado ou é um diretório: $caminhoDoArquivo")
        return emptyList()
    }

    return try {
        // 'readLines()' é uma função do Kotlin que lê todas as linhas
        // do arquivo e já as retorna como List<String>
        arquivo.readLines()
    } catch (e: SecurityException) {
        println("ERRO: Sem permissão para ler o arquivo: ${e.message}")
        emptyList()
    } catch (e: Exception) {
        println("ERRO: Ocorreu um erro inesperado ao ler o arquivo: ${e.message}")
        emptyList()
    }
}