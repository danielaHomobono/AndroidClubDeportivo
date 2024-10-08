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
    private lateinit var spinnerDni: Spinner
    private lateinit var spinnerActividad: Spinner
    private lateinit var spinnerEstado: Spinner
    private lateinit var btnMostrar: Button
    private lateinit var btnHome: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profesores)

        initializeViews()
        setupHomeButton()
        setupSpinner()

    }

    private fun initializeViews() {
        spinnerApellido = findViewById(R.id.spinnerApellido)
        spinnerDni = findViewById(R.id.spinnerDni)
        spinnerActividad = findViewById(R.id.spinnerActividad)
        spinnerEstado = findViewById(R.id.spinnerEstado)
        btnMostrar = findViewById(R.id.btnMostrar)
        btnHome = findViewById(R.id.homeButton)
    }


    private fun setupSpinner() {
        val adapterApellido = ArrayAdapter.createFromResource(
            this,
            R.array.profesor_apellido_array,
            android.R.layout.simple_spinner_item
        )
        adapterApellido.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerApellido.adapter = adapterApellido


        val adapterDni = ArrayAdapter.createFromResource(
            this,
            R.array.profesor_dni_array,
            android.R.layout.simple_spinner_item
        )
        adapterDni.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDni.adapter = adapterDni


        val adapterActividad = ArrayAdapter.createFromResource(
            this,
            R.array.profesor_actividad_array,
            android.R.layout.simple_spinner_item
        )
        adapterActividad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerActividad.adapter = adapterActividad


        val adapterEstado = ArrayAdapter.createFromResource(
            this,
            R.array.profesor_estado_array,
            android.R.layout.simple_spinner_item
        )
        adapterEstado.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerEstado.adapter = adapterEstado
    }

    private fun setupHomeButton() {
        btnHome.setOnClickListener {
            val intent = Intent(this, Home::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }

    }
}
