package com.example.androidclubdeportivo.controller

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.androidclubdeportivo.R

class Home : AppCompatActivity() {

    private lateinit var tvUsername: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Initialize the TextView
        tvUsername = findViewById(R.id.tvUsername)


        val username = intent.getStringExtra("USERNAME") ?: ""
        tvUsername.text = username

        val btnGestionClientes = findViewById<Button>(R.id.btnGestionClientes)
        btnGestionClientes.setOnClickListener {
            val intent = Intent(this, GestionCliente::class.java)
            startActivity(intent)
        }

        val btnGestionActividades = findViewById<Button>(R.id.btnGestionActividades)
        btnGestionActividades.setOnClickListener {
            val intent = Intent(this, GestionActividades::class.java)
            startActivity(intent)
        }

        val btnGestionPagos = findViewById<Button>(R.id.btnGestionPagos)
        btnGestionPagos.setOnClickListener {
            val intent = Intent(this, GestionPagos::class.java)
            startActivity(intent)
        }

        val imglogout = findViewById<ImageView>(R.id.logout)
        imglogout.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
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

    private fun sendEmail(email: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            putExtra(Intent.EXTRA_SUBJECT, "Consulta a la sucursal")
            putExtra(Intent.EXTRA_TEXT, "Hola, me gustaría hacer una consulta.")
        }

        try {
            startActivity(Intent.createChooser(intent, "Enviar email..."))
        } catch (ex: android.content.ActivityNotFoundException) {
            Toast.makeText(this, "No tienes clientes de correo instalados.", Toast.LENGTH_SHORT)
                .show()
        }
    }
}