package com.example.segundoaplicativo

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.segundoaplicativo.databinding.ActivityCatDogBinding

class CatDogActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var binding: ActivityCatDogBinding
    var catSelected: Boolean = true

    val frasesCachorro: List<String> = listOf<String>(
        "Diferente dos humanos, os cães não suam pelo corpo todo. A principal forma de liberar calor (além da respiração ofegante) é através das glândulas sudoríparas localizadas nas almofadas das patas.",
        "A raça Basenji é conhecida como o \"cão sem latido\". Eles não latem como os outros cães, mas emitem um som único, parecido com um uivo ou canto (conhecido como \"yodel\").",
        "O Lundehund Norueguês é uma raça única que possui seis dedos em cada pata, em vez dos quatro ou cinco habituais (contando o \"dedo\" extra, ou ergô).",
        "O bocejo de um cachorro pode ser um sinal de empatia. Eles são mais propensos a bocejar depois de ver um humano (especialmente seu dono) bocejar."
    )

    val frasesGato: List<String> = listOf<String>(
        "Os gatos não possuem os receptores gustativos para o sabor doce. Por isso, eles não têm interesse em açúcar ou frutas da mesma forma que nós.",
        "Gatos não possuem clavículas funcionais como as nossas. Essa característica, combinada com ombros flexíveis, permite que eles passem por qualquer abertura que seja do tamanho da sua cabeça.",
        "Estima-se que os gatos possam produzir cerca de 100 sons vocais diferentes (miados, ronronados, grunhidos, etc.), enquanto os cães produzem apenas cerca de 10.",
        "Assim como os humanos, os gatos podem ter uma pata dominante. Estudos sugerem que a maioria das gatas fêmeas são \"destras\" (usam mais a pata direita), enquanto os machos são mais propensos a ser \"canhotos\" (usam mais a pata esquerda)."
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cat_dog)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding = ActivityCatDogBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.gerarOutraFraseButton.setOnClickListener(this)
        binding.imagemGato.setOnClickListener(this)
        binding.imagemCachorro.setOnClickListener(this)

        gerarFraseCachorroAleatorio()
        binding.imagemCachorro.setColorFilter(ContextCompat.getColor(this, R.color.yellow))

        val sharedPreferences = getSharedPreferences("nome", MODE_PRIVATE)
        binding.olaVisitante.text = "Olá, ${sharedPreferences.getString("nome", "Visitante!")}"
    }

    override fun onClick(v: View) {

        if (v.id == binding.gerarOutraFraseButton.id) {
            if (catSelected == true) {
                gerarFraseGatoAleatorio()
            } else {
                gerarFraseCachorroAleatorio()
            }
        } else if (v.id == binding.imagemGato.id) {
            binding.imagemGato.setColorFilter(ContextCompat.getColor(this, R.color.yellow))
            binding.imagemCachorro.colorFilter = null

            if (!catSelected) {
                gerarFraseCachorroAleatorio()
            }

            catSelected = true

        } else if (v.id == binding.imagemCachorro.id) {
            binding.imagemCachorro.setColorFilter(ContextCompat.getColor(this, R.color.yellow))
            binding.imagemGato.colorFilter = null
            if (catSelected) {
                gerarFraseCachorroAleatorio()
            }
            catSelected = false
        }
    }

    fun gerarFraseGatoAleatorio() {
        binding.frase.text = frasesGato.random()
    }

    fun gerarFraseCachorroAleatorio() {
        binding.frase.text = frasesCachorro.random()
    }
}