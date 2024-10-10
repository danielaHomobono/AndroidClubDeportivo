package com.example.androidclubdeportivo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class GestionCliente : AppCompatActivity() {
    private lateinit var btnHome: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gestion_cliente)


        setupUI()
        setupHomeButton()

    }

    private fun setupUI() {
        val btnInscripcion = findViewById<Button>(R.id.gestion_clientes_button)
        btnInscripcion.setOnClickListener {
            val intent = Intent(this, InscripcionCliente::class.java)
            startActivity(intent)
        }

        val btnModificacionDatos = findViewById<Button>(R.id.gestion_actividades_button)
        btnModificacionDatos.setOnClickListener {
            val intent = Intent(this, ModificacionCliente::class.java)
            startActivity(intent)
            Toast.makeText(this, "Funcionalidad de Modificación de Datos en desarrollo", Toast.LENGTH_SHORT).show()
        }

        val btnImpresionCarnet = findViewById<Button>(R.id.gestion_pagos_button)
        btnImpresionCarnet.setOnClickListener {
            val intent = Intent(this, ImpresCarnet::class.java)
            startActivity(intent)
            Toast.makeText(this, "Funcionalidad de Impresión de Carnet en desarrollo", Toast.LENGTH_SHORT).show()
        }




        val sucursalCerro = findViewById<LinearLayout>(R.id.sucursal_cerro)
        sucursalCerro.setOnClickListener {
            sendEmail("sucursal.cerro@example.com")
        }

        val sucursalCordoba = findViewById<LinearLayout>(R.id.sucursal_cordoba)
        sucursalCordoba.setOnClickListener {
            sendEmail("sucursal.cordoba@example.com")
        }
    }
    private fun setupHomeButton() {
        btnHome = findViewById(R.id.homeButton)
        btnHome.setOnClickListener {
            // Crear un Intent para volver a la actividad Home
            val intent = Intent(this, Home::class.java)
            // Limpiar la pila de actividades y poner Home en la parte superior
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            // Opcional: agregar una animación de transición
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            // Finalizar la actividad actual
            finish()
        }
    }
    private fun sendEmail(email: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            putExtra(Intent.EXTRA_SUBJECT, "Consulta sobre Gestión de Clientes")
            putExtra(Intent.EXTRA_TEXT, "Hola, me gustaría hacer una consulta sobre la gestión de clientes.")
        }

        try {
            startActivity(Intent.createChooser(intent, "Enviar email..."))
        } catch (ex: android.content.ActivityNotFoundException) {
            Toast.makeText(this, "No tienes clientes de correo instalados.", Toast.LENGTH_SHORT).show()
        }
    }
}