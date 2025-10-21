package domain

class Roupa : Produto() {
    enum class Tipo
    {
        CAMISA,
        MOLETOM,
        ACESSORIO
    }
    lateinit var tipo: Tipo

    enum class Tamanho
    {
        PP,
        P,
        M,
        G,
        GG,
        XG,
        XXG
    }
    lateinit var tamanho: Tamanho

    var corPrimaria: String = ""

    var corSecundaria: String? = null
}