package com.example.appclima

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.appclima.databinding.ActivitySearchScreenBinding

class SearchScreenActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivitySearchScreenBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search_screen)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_search_screen)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        sharedPreferences = getSharedPreferences("nome_cidade", MODE_PRIVATE)

        binding = ActivitySearchScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonPrevisao.setOnClickListener(this)

        if (sharedPreferences.contains("nome_cidade")) {
            val intent = Intent(this, ReportScreenActivity::class.java)
            startActivity(intent)
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
}