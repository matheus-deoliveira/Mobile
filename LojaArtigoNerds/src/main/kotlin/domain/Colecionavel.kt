package domain

class Colecionavel : Produto() {

    enum class Tipo
    {
        LIVRO,
        BONECO,
        OUTROS
    }

    lateinit var tipo: Tipo

    enum class MaterialFabricacao
    {
        PAPEL,
        PLASTICO,
        ACO,
        MISTURADO,
        OUTROS
    }

    lateinit var materialFabricacao: MaterialFabricacao

    var tamanhoEmCentimetros: Int = 0

    enum class Relevancia
    {
        COMUM,
        MEDIO,
        RARO,
        RARISSIMO
    }

    lateinit var relevancia: Relevancia
}