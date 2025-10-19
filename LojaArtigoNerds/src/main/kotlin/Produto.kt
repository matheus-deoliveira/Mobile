package org.example

open class Produto {
    var nome: String = ""
    var precoComprado: Double = 0.0
    var precoVendido: Double = 0.0
    var code: String = ""

    /**
     * O código original do produto, vindo do fornecedor.
     * Ex: "RGL-001"
     */
    var codigoOriginal: String = ""

    /**
     * O código final formatado pelo sistema, com prefixo da categoria .
     * Ex: "R-RGL-001"
     */
    var codigoFormatado: String = ""
}