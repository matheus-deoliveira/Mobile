package com.example.segundoaplicativo

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.segundoaplicativo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        sharedPreferences = getSharedPreferences("nome", MODE_PRIVATE)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.guardarButton.setOnClickListener(this)

        if (sharedPreferences.contains("nome")) {
            val intent = Intent(this, CatDogActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onClick(v: View) {
        if (v.id == binding.guardarButton.id) {
            val nome = binding.nomeFieldEditText.text.toString()

            val edit = sharedPreferences.edit()
            edit.putString("nome", nome)
            edit.apply()

            val intent = Intent(this, CatDogActivity::class.java)
            startActivity(intent)
        }
    }
}