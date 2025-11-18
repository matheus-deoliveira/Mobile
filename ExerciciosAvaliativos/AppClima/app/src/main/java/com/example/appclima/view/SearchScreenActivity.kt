package com.example.appclima.view

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.appclima.R
import com.example.appclima.databinding.ActivitySearchScreenBinding
import com.example.appclima.viewmodel.SearchScreenViewModel

class SearchScreenActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivitySearchScreenBinding
    private lateinit var sharedPreferences: SharedPreferences

    private val viewModel: SearchScreenViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        sharedPreferences = getSharedPreferences("AppClimaPrefs", MODE_PRIVATE)

        binding = ActivitySearchScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupLayout()

        setupObservers()

        binding.buttonPrevisao.setOnClickListener {
            val cidade = binding.nomeCidade.text.toString()

            viewModel.validarCidade(cidade)
        }
    }

    override fun onClick(v: View) {
        if (v.id == binding.buttonPrevisao.id) {
            val cidade = binding.nomeCidade.text.toString()

            val edit = sharedPreferences.edit()
            edit.putString("nome_cidade", cidade)
            edit.apply()

            val intent = Intent(this, ReportScreenActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupObservers() {

        viewModel.mensagemErro.observe(this) { msg ->
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
            binding.nomeCidade.error = "Campo obrigatório"
        }

        viewModel.cidadeEncontrada.observe(this) { cidadeValidada ->
            salvarENavegar(cidadeValidada)
        }
    }

    private fun salvarENavegar(cidade: String) {

        val edit = sharedPreferences.edit()
        edit.putString("nome_cidade", cidade)
        edit.apply()

        val intent = Intent(this, ReportScreenActivity::class.java)
        startActivity(intent)
    }

    private fun setupLayout() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_search_screen)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}