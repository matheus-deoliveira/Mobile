package org.example

/**
 * Constante global para controlar os logs de depuração de todo o projeto.
 * Mude para 'false' para desativar todas as mensagens de depuração no terminal.
 */
internal const val MODO_DEBUG = true

/**
 * Imprime uma mensagem no console somente se o MODO_DEBUG estiver ativado.
 */
internal fun logDebug(mensagem: String) {
    if (MODO_DEBUG) {
        println(mensagem)
    }
}