package com.example.androidclubdeportivo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.EditText
import android.widget.CheckBox
import android.widget.LinearLayout






class Login : AppCompatActivity() {

    private lateinit var etUser: EditText
    private lateinit var etPassword: EditText
    private lateinit var cbRememberMe: CheckBox
    private lateinit var btnSubscribe: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)
        etUser = findViewById(R.id.etUser)
        etPassword = findViewById(R.id.etPassword)
        cbRememberMe = findViewById(R.id.cbRememberMe)
        btnSubscribe = findViewById(R.id.btnSubscribe)


        val sucursalCerro = findViewById<LinearLayout>(R.id.sucursal_cerro)
        sucursalCerro.setOnClickListener {
            sendEmail("sucursal.cerro@example.com")
        }

        // Enviar correo - Sucursal Córdoba
        val sucursalCordoba = findViewById<LinearLayout>(R.id.sucursal_cordoba)
        sucursalCordoba.setOnClickListener {
            sendEmail("sucursal.cordoba@example.com")
        }

                val btnSubscribe = findViewById<Button>(R.id.btnSubscribe)
                btnSubscribe.setOnClickListener{
                    if (validateInput()) {
                    val intent= Intent(this, Home::class.java)
                    startActivity(intent)
            } else {
                Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun validateInput(): Boolean {
        val username = etUser.text.toString().trim()
        val password = etPassword.text.toString().trim()

        return username.isNotEmpty() && password.isNotEmpty()
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

