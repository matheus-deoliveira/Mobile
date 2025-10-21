package domain

class Eletronico : Produto() {
    enum class Tipo
    {
        VIDEO_GAME,
        JOGO,
        PORTATIL,
        OUTROS
    }

    lateinit var tipo: Tipo

    var versao: String = ""

    var anoDeFabricacao: Int = 0
}