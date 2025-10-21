package domain

open class Produto {
    var nome: String = ""
    var precoComprado: Double = 0.0
    var precoVendido: Double = 0.0

    /**
     * O código original do produto, vindo do fornecedor.
     */
    var codigoOriginal: String = ""

    /**
     * O código final formatado pelo sistema, com prefixo da categoria .
     */
    var codigoFormatado: String = ""
}