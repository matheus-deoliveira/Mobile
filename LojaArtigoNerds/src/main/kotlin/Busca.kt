import domain.Colecionavel
import domain.Eletronico
import domain.ItemEstoque
import domain.Produto
import domain.Roupa
import org.example.logDebug

class Busca {

    /**
     * Processa um arquivo de filtros (busca.csv) contra o estoque existente.
     * Salva um resultado_busca em "resultado_busca.csv".
     *
     * @param linhasBusca O conteúdo bruto do 'busca.csv' (incluindo cabeçalho).
     * @param estoque O mapa de estoque consolidado.
     * @param pastaSaida A pasta onde o 'resultado_busca.csv' será salvo.
     */
    fun realizarBusca(
        linhasBusca: List<String>,
        estoque: Map<String, ItemEstoque>,
        pastaSaida: String
    ) {
        // Pula o cabeçalho e filtra linhas em branco
        val linhasDeFiltro = linhasBusca.drop(1).filter { it.isNotBlank() }
        logDebug("\nIniciando contagem de busca para ${linhasDeFiltro.size} filtros...")

        // Lista que guardará os resultados
        val resultadosContagem = mutableListOf<String>()

        for ((index, linhaFiltro) in linhasDeFiltro.withIndex()) {

            val filtroNumero = index + 1 // Para o relatório (Filtro 1, Filtro 2, ...)
            var matchCount = 0 // Zera a contagem para este filtro

            // Pega os critérios da linha de filtro atual
            val criterios = linhaFiltro.split(",").map { it.trim() }

            // Garantia de segurança contra linhas mal formatadas
            if (criterios.size < 9) {
                logDebug("AVISO: Linha de filtro $filtroNumero está mal formatada. Pulando.")
                continue
            }

            // Agora, para este filtro, iteramos pelo estoque
            for (itemEstoque in estoque.values) {
                val produto = itemEstoque.produto

                // Usamos a mesma função de antes para ver se o produto "bate"
                if (produtoMatchesCriterios(produto, criterios)) {
                    // Se bateu, incrementa o contador DESTE FILTRO
                    matchCount += itemEstoque.quantidade
                }
            }

            // Após testar o filtro contra o estoque, salvamos o resultado
            resultadosContagem.add("$filtroNumero,$matchCount")
            logDebug("   - Filtro $filtroNumero encontrou $matchCount itens.")
        }

        logDebug("Contagem de busca concluída.")

        // Salva o relatório de contagem
        salvarRelatorioBusca(resultadosContagem, pastaSaida)
    }

    /**
     * Verifica se um único produto corresponde a uma única linha de critérios do busca.csv.
     */
    private fun produtoMatchesCriterios(produto: Produto, criterios: List<String>): Boolean { //
        val catFiltro = criterios[0]
        val tipoFiltro = criterios[1]
        val tamFiltro = criterios[2]
        val cor1Filtro = criterios[3]
        val cor2Filtro = criterios[4]
        val verFiltro = criterios[5]
        val anoFiltro = criterios[6]
        val matFiltro = criterios[7]
        val relFiltro = criterios[8]

        // Filtro de Categoria
        if (catFiltro != "-") {
            if (catFiltro.equals("colecionavel", true) && produto !is Colecionavel) return false //
            if (catFiltro.equals("roupa", true) && produto !is Roupa) return false //
            if (catFiltro.equals("eletronico", true) && produto !is Eletronico) return false //
        }

        // Filtros Específicos por Atributo
        when (produto) {
            is Colecionavel -> {
                if (tipoFiltro != "-" || tamFiltro != "-" || cor1Filtro != "-" || cor2Filtro != "-") return false // Filtros de Roupa
                if (verFiltro != "-" || anoFiltro != "-") return false // Filtros de Eletronico

                if (matFiltro != "-" && !produto.materialFabricacao.name.equals(matFiltro, true)) return false
                if (relFiltro != "-" && !produto.relevancia.name.equals(relFiltro, true)) return false
            }
            is Roupa -> {
                if (matFiltro != "-" || relFiltro != "-") return false // Filtros de Colecionavel
                if (verFiltro != "-" || anoFiltro != "-") return false // Filtros de Eletronico

                if (tipoFiltro != "-" && !produto.tipo.name.equals(tipoFiltro, true)) return false
                if (tamFiltro != "-" && !produto.tamanho.name.equals(tamFiltro, true)) return false
                if (cor1Filtro != "-" && !produto.corPrimaria.equals(cor1Filtro, true)) return false
                if (cor2Filtro != "-" && !produto.corSecundaria.equals(cor2Filtro, true)) return false
            }
            is Eletronico -> {
                if (tipoFiltro != "-" || tamFiltro != "-" || cor1Filtro != "-" || cor2Filtro != "-") return false // Filtros de Roupa
                if (matFiltro != "-" || relFiltro != "-") return false // Filtros de Colecionavel

                if (verFiltro != "-" && !produto.versao.equals(verFiltro, true)) return false
                if (anoFiltro != "-" && produto.anoDeFabricacao != anoFiltro.toIntOrNull()) return false
            }
        }
        return true
    }

    /**
     * Salva o resultado_busca no formato "BUSCAS,QUANTIDADE".
     */
    private fun salvarRelatorioBusca(
        resultados: List<String>,
        pastaSaida: String
    ) {
        val header = "BUSCAS,QUANTIDADE"

        // Junta o cabeçalho ("BUSCAS,QUANTIDADE") com as linhas de resultado
        val conteudoCompleto = (listOf(header) + resultados).joinToString("\n")

        val caminhoArquivoSaida = "$pastaSaida/resultado_busca.csv"

        escreverArquivo(caminhoArquivoSaida, conteudoCompleto)

        logDebug("Relatório de busca salvo com sucesso em: $caminhoArquivoSaida")
    }
}