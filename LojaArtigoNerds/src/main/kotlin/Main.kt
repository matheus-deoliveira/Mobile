import domain.ItemEstoque
import domain.Produto
import org.example.logDebug
import kotlin.system.exitProcess

private lateinit var estoque: Map<String, ItemEstoque>

fun main (args: Array<String>){

    // Instanciar os processadores
    val processadorCompras = ProcessadorCompras()
    val processadorVendas = ProcessadorVendas()
    val gerenciadorEstoque = GerenciadorEstoque()
    val balancete = Balancete()
    val busca = Busca()

    // Para depuração, vamos imprimir o diretório de trabalho atual
    logDebug("Diretório de Trabalho Atual: ${System.getProperty("user.dir")}")

    val (pastaEntrada, pastaSaida) = validarEObterPastas(args)

    val caminhoCompras = "$pastaEntrada/compras.csv"
    val caminhoVendas = "$pastaEntrada/vendas.csv"

    // Processamento de Compras
    logDebug("\nLendo arquivo de compras em: $caminhoCompras")
    val linhasCompras = lerArquivoCsv (caminhoCompras)
    var produtosComprados: List<Pair<Produto, Int>> = emptyList()

    produtosComprados = processadorCompras.processarCompras(linhasCompras)

    // Processamento de Vendas
    logDebug("\nLendo arquivo de vendas em: $caminhoVendas")
    val linhasVendas : List<String> = lerArquivoCsv (caminhoVendas)
    var produtosVendidos: List<Pair<String, Int>> = emptyList()

    produtosVendidos = processadorVendas.processarVendas(linhasVendas)

    // Gerenciamento de Compra e Venda
    estoque = gerenciadorEstoque.consolidarEstoque(produtosComprados, produtosVendidos)

    estoque.forEach { (codigo, item) ->
        logDebug("   - [$codigo] ${item.produto.nome}: ${item.quantidade} unidades")
    }

    // Gerenciamento de Estoque Geral
    gerenciadorEstoque.salvarEstoqueGeral(estoque, pastaSaida)

    // Gerenciamento de Estoque por Categoria
    gerenciadorEstoque.salvarEstoquePorCategoria(estoque, pastaSaida)

    // Balancete da Loja
    balancete.balancete(produtosComprados, produtosVendidos, pastaSaida)

    // Preciso ler o e fazer a busca somente se busca.csv estiver no diretório de entrada

    // Sistema de Busca

    val caminhoBusca = "$pastaEntrada/busca.csv"

    val arquivoBusca = java.io.File(caminhoBusca)

    // Verifica se o arquivo realmente existe e é um arquivo (não uma pasta)
    if (arquivoBusca.exists() && arquivoBusca.isFile) {

        logDebug("\nArquivo 'busca.csv' encontrado. Iniciando sistema de busca...")

        val linhasBusca = lerArquivoCsv(caminhoBusca)

        busca.realizarBusca(linhasBusca, estoque, pastaSaida)

    } else {
        logDebug("\nArquivo 'busca.csv' não encontrado em '$pastaEntrada'. Pulando etapa de busca.")
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
    return Pair(args[0], args[1])
}