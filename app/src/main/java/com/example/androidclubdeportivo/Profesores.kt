package com.example.androidclubdeportivo

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageButton

class Profesores : AppCompatActivity() {

    private lateinit var spinnerApellido: Spinner
    private lateinit var spinnerActividad: Spinner
    private lateinit var spinnerEstado: Spinner
    private lateinit var btnMostrar: Button
    private lateinit var btnHome: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profesores)

        initializeViews()
        setupHomeButton()
        setupSpinners()
        setupMostrarButton()
    }

    private fun initializeViews() {
        spinnerApellido = findViewById(R.id.spinnerApellido)
        spinnerActividad = findViewById(R.id.spinnerActividad)
        spinnerEstado = findViewById(R.id.spinnerEstado)
        btnMostrar = findViewById(R.id.btnMostrar)
        btnHome = findViewById(R.id.homeButton)
    }

    private fun setupSpinners() {
        setupSpinner(spinnerApellido, R.array.profesor_apellido_array)
        setupSpinner(spinnerActividad, R.array.actividad_array)
        setupSpinner(spinnerEstado, R.array.profesor_estado_array)
    }

    private fun setupSpinner(spinner: Spinner, arrayResourceId: Int) {
        ArrayAdapter.createFromResource(
            this,
            arrayResourceId,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }
    }

    private fun setupHomeButton() {
        btnHome.setOnClickListener {
            val intent = Intent(this, Home::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }
    }

    private fun setupMostrarButton() {
        btnMostrar.setOnClickListener {
            val apellido = spinnerApellido.selectedItem.toString()
            val actividad = spinnerActividad.selectedItem.toString()
            val estado = spinnerEstado.selectedItem.toString()

            // Create an Intent to start Profesores2 activity
            val intent = Intent(this, Profesores2::class.java).apply {
                putExtra("APELLIDO", apellido)
                putExtra("ACTIVIDAD", actividad)
                putExtra("ESTADO", estado)
            }
            startActivity(intent)
        }
    }
}