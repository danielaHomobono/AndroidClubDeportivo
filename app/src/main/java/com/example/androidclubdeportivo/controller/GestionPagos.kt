package com.example.androidclubdeportivo.controller

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import com.example.androidclubdeportivo.R

class GestionPagos : AppCompatActivity() {

    private lateinit var homeButton: ImageButton
    private lateinit var btnPagar: Button
    private lateinit var btnReportes: Button
    private lateinit var sucursalCerro: TextView
    private lateinit var sucursalCordoba: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gestion_pagos) // Assuming your layout file is named activity_pagar.xml

        // Initialize views
        homeButton = findViewById(R.id.homeButton)
        btnPagar = findViewById(R.id.btnPagar)
        btnReportes = findViewById(R.id.btnReportes)
        sucursalCerro = findViewById(R.id.sucursal_cerro)
        sucursalCordoba = findViewById(R.id.sucursal_cordoba)

        // Set up click listeners
        homeButton.setOnClickListener {
            // Navigate to home screen
            val intent = Intent(this, Home::class.java) // Replace MainActivity with your actual home activity
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }

        btnPagar.setOnClickListener {

             val intent = Intent(this, Pagar::class.java)
             startActivity(intent)
        }

        btnReportes.setOnClickListener {

             val intent = Intent(this, Reportes::class.java)
             startActivity(intent)
        }

        // You can add more functionality here, such as updating the sucursal information
        updateSucursalInfo()
    }

    private fun updateSucursalInfo() {

        sucursalCerro.text = "Sucursal Cerro: Abierta"
        sucursalCordoba.text = "Sucursal Córdoba: Cerrada"
    }
}