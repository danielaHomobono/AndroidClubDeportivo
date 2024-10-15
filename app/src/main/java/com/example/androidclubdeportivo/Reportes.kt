package com.example.androidclubdeportivo


import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class Reportes : AppCompatActivity() {

    // Declarar variables para los elementos de la interfaz
    private lateinit var headerTitle: TextView
    private lateinit var homeButton: ImageButton
    private lateinit var spinnerActividad: Spinner
    private lateinit var spinnerEstado: Spinner
    private lateinit var btnMostrar: Button
    private lateinit var btnHome: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reportes)  // Reemplazar con el nombre correcto de tu layout


        headerTitle = findViewById(R.id.headerTitle)
        homeButton = findViewById(R.id.homeButton)
        spinnerActividad = findViewById(R.id.spinnerActividad)
        spinnerEstado = findViewById(R.id.spinnerEstado)
        btnMostrar = findViewById(R.id.btnMostrar)
        btnHome = findViewById(R.id.homeButton)

        setupSpinnerActividad()
        setupSpinnerEstado()
        setupHomeButton()
        btnMostrar.setOnClickListener {
            mostrarResultados()
        }


    }


    private fun setupSpinnerActividad() {
        val actividades = listOf("Pago de Cuotas", "Deudas de Profesores", "Otro")
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            actividades
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerActividad.adapter = adapter
    }


    private fun setupSpinnerEstado() {
        val estados = listOf("Vencido", "Al día")
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            estados
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerEstado.adapter = adapter
    }

    // Función que muestra los resultados según las selecciones
    private fun mostrarResultados() {
        val actividadSeleccionada = spinnerActividad.selectedItem.toString()
        val estadoSeleccionado = spinnerEstado.selectedItem.toString()


        if (actividadSeleccionada == "Pago de Cuotas" && estadoSeleccionado == "Vencido") {

        } else if (actividadSeleccionada == "Deudas de Profesores" && estadoSeleccionado == "Al día") {

        }

        val intent = Intent(this, Reportes2::class.java)
        intent.putExtra("actividadSeleccionada", actividadSeleccionada)
        intent.putExtra("estadoSeleccionado", estadoSeleccionado)
        startActivity(intent)
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
