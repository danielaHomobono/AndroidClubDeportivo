package com.example.androidclubdeportivo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageButton
import android.widget.Button
import android.widget.Toast

class ImpresCarnet : AppCompatActivity() {

    private lateinit var btnHome: ImageButton
    private lateinit var btnImprimir: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_impres_carnet)

        initializeViews()
        setupListeners()
    }

    private fun initializeViews() {
        btnHome = findViewById(R.id.homeButton)
        btnImprimir = findViewById(R.id.btnSubscribe)
    }

    private fun setupListeners() {
        btnHome.setOnClickListener {
            val intent = Intent(this, Home::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }

        btnImprimir.setOnClickListener {
            // Aquí iría la lógica para imprimir el carnet
            Toast.makeText(this, "Imprimiendo carnet...", Toast.LENGTH_SHORT).show()
        }
    }
}