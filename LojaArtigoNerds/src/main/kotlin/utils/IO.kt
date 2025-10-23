package utils

import java.io.File

/**
 * Lê um arquivo (como um CSV) a partir do caminho completo e retorna seu conteúdo
 * como uma List<String>, onde cada string é uma linha do arquivo.
 */
internal fun lerArquivoCsv(caminhoDoArquivo: String): List<String> {
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
 * Cria o arquivo se não existir ou sobrescreve se existir.
 */
internal fun escreverArquivo(caminhoDoArquivo: String, conteudo: String) {
    val arquivo = File(caminhoDoArquivo)

    // Garante que os diretórios pais existam
    try {
        arquivo.parentFile?.mkdirs()
    } catch (e: SecurityException) {
        println("ERRO: Sem permissão para criar diretórios para: $caminhoDoArquivo")
        return
    }

    // Tenta escrever o arquivo
    try {
        arquivo.writeText(conteudo)
    } catch (e: SecurityException) {
        println("ERRO: Sem permissão para escrever o arquivo: ${e.message}")
    } catch (e: Exception) {
        println("ERRO: Ocorreu um erro inesperado ao escrever o arquivo: ${e.message}")
    }
}