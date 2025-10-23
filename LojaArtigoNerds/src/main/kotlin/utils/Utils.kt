package utils

import java.text.Normalizer

/**
 * Remove acentos de uma string, conforme regra do projeto.
 */
internal fun String.semAcentos(): String {
    val normalizado = Normalizer.normalize(this, Normalizer.Form.NFD)
    return normalizado.replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "")
}

/**
 * Processa um campo de texto que pode ser opcional.
 * Se o valor for "-", retorna null.
 * Também aplica as regras de maiúsculo e remoção de acentos.
 */
internal fun processarCampoOpcional(valor: String): String? {
    val valorProcessado = valor.uppercase().semAcentos()
    return if (valorProcessado == "-") null else valorProcessado
}

/**
 * Processa um campo numérico (Int) que pode ser "-".
 * Se for "-", retorna 0. Caso contrário, converte para Int.
 */
internal fun processarCampoNumerico(valor: String): Int {
    // Remove espaços em branco que podem vir do CSV antes de checar
    return if (valor.trim() == "-") 0 else valor.trim().toInt()
}

/**
 * Processa a string para ser usada em um Enum.valueOf().
 * Remove acentos, coloca em maiúsculo e substitui hífens por underscores.
 */
internal fun String.paraEnum(): String {
    return this.semAcentos().uppercase().replace("-", "_")
}