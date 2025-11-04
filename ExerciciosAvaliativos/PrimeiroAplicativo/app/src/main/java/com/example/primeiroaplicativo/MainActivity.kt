package com.example.primeiroaplicativo

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.primeiroaplicativo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.calcularButton.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        if (v.id == R.id.calcularButton) {
            var salario: Float = 0F
            var gastos: Float = 0F
            var dependentes: Int = 0

            try {
                salario = binding.salarioEditText.text.toString().toFloat()
                gastos = binding.gastosEditText.text.toString().toFloat()
                dependentes = binding.dependentesEditText.text.toString().toInt()

                if (salario <= 0 || gastos < 0 || dependentes < 0) {
                    throw Exception()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this,
                    getString(R.string.erro_calculo), Toast.LENGTH_SHORT
                ).show()
                return
            }

            var aliquota: Float = 0F
            var deducao: Float = 0F
            var base: Float = salario - (dependentes * 189.59F) - gastos

            if (base <= 2428.80) {
                binding.impostoValueTextView.text = "Não precisa pagar imposto"
                return
            } else if (base > 2428.80 && base <= 2826.65) {
                aliquota = 7.5F
                deducao = 182.16F
            } else if (base > 2826.66 && base <= 3751.05) {
                aliquota = 15F
                deducao = 394.16F
            } else if (base > 3751.06 && base <= 4664.68) {
                aliquota = 22.5F
                deducao = 675.49F
            }
            else { // salario > 4664.68
                aliquota = 27.5F
                deducao = 908.73F
            }

            var IR = (base * (aliquota / 100)) - deducao
            binding.impostoValueTextView.text = "R$ %.2f".format(IR).toString().replace(".", ",")
        }
    }
}