package com.example.androidclubdeportivo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class GestionActividades : AppCompatActivity() {

    private lateinit var btnInscripcion: Button
    private lateinit var btnHorarios: Button
    private lateinit var btnProfesores: Button
    private lateinit var btnHome: ImageButton
    private lateinit var tvSucursalCerro: TextView
    private lateinit var tvSucursalCordoba: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gestion_actividades)

        initializeViews()
        setupListeners()
    }

    private fun initializeViews() {
        btnInscripcion = findViewById(R.id.btnInscripcion)
        btnHorarios = findViewById(R.id.btnHorarios)
        btnProfesores = findViewById(R.id.btnProfesores)
        btnHome = findViewById(R.id.homeButton)
        tvSucursalCerro = findViewById(R.id.sucursal_cerro)
        tvSucursalCordoba = findViewById(R.id.sucursal_cordoba)
    }

    private fun setupListeners() {
        btnInscripcion.setOnClickListener {
            val intent = Intent(this, InscripcionActividad::class.java)
            startActivity(intent)
        }

        btnHorarios.setOnClickListener {
            val intent = Intent(this, Horarios::class.java)
            startActivity(intent)
        }

        btnProfesores.setOnClickListener {
            val intent = Intent(this, Profesores::class.java)
            startActivity(intent)
        }

        btnHome.setOnClickListener {
            val intent = Intent(this, Home::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }

        tvSucursalCerro.setOnClickListener {
            sendEmail("sucursal.cerro@example.com")
        }

        tvSucursalCordoba.setOnClickListener {
            sendEmail("sucursal.cordoba@example.com")
        }
    }

    private fun sendEmail(email: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            putExtra(Intent.EXTRA_SUBJECT, "Consulta sobre Gestión de Actividades")
            putExtra(Intent.EXTRA_TEXT, "Hola, me gustaría hacer una consulta sobre las actividades del club.")
        }

        try {
            startActivity(Intent.createChooser(intent, "Enviar email..."))
        } catch (ex: android.content.ActivityNotFoundException) {
            Toast.makeText(this, "No tienes clientes de correo instalados.", Toast.LENGTH_SHORT).show()
        }
    }
}