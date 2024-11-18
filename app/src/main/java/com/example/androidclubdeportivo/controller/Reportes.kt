package com.example.androidclubdeportivo.controller

import ClienteAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androidclubdeportivo.R
import com.example.androidclubdeportivo.model.Cliente
import com.example.androidclubdeportivo.model.dao.ActividadDAO
import com.example.androidclubdeportivo.model.dao.ClienteDAO
import com.example.androidclubdeportivo.model.dao.ClubDatabaseHelper

class Reportes : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ClienteAdapter
    private lateinit var spinnerEstado: Spinner
    private lateinit var btnMostrar: Button
    private lateinit var clienteDAO: ClienteDAO
    private lateinit var actividadDAO: ActividadDAO
    private lateinit var btnHome: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reportes)

        // Inicializmos
        btnHome = findViewById(R.id.homeButton)
        recyclerView = findViewById(R.id.recyclerViewClientes)
        spinnerEstado = findViewById(R.id.spinnerEstado)
        btnMostrar = findViewById(R.id.btnMostrar)


        clienteDAO = ClienteDAO(ClubDatabaseHelper(this))
        actividadDAO = ActividadDAO(ClubDatabaseHelper(this))

        // Configuración del RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Configuración del Spinner
        val estados = arrayOf("Cuotas Vencidas", "Cuotas Al Día", "Cuotas Vencen Hoy")
        val adapterSpinner = ArrayAdapter(this, android.R.layout.simple_spinner_item, estados)
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerEstado.adapter = adapterSpinner

        setupHomeButton()

        btnMostrar.setOnClickListener {
            mostrarClientesSegunEstado()
        }
    }

    private fun mostrarClientesSegunEstado() {
        val estadoSeleccionado = spinnerEstado.selectedItem.toString()

        try {
            // Obtener lista de clientes según el estado seleccionado
            val clientes: List<Cliente> = when (estadoSeleccionado) {
                "Cuotas Vencidas" -> clienteDAO.getClientesConCuotasVencidas()
                "Cuotas Al Día" -> clienteDAO.getClientesAlDia()
                "Cuotas Vencen Hoy" -> clienteDAO.getClientesConCuotasVencenHoy()
                else -> emptyList()
            }

            Log.d("ReportesActivity", "Estado seleccionado: $estadoSeleccionado")
            Log.d("ReportesActivity", "Clientes obtenidos: ${clientes.size}")

            if (clientes.isEmpty()) {
                Toast.makeText(this, "No se encontraron clientes para el estado seleccionado", Toast.LENGTH_SHORT).show()
                recyclerView.visibility = View.GONE // Ocultar RecyclerView si no hay datos
            } else {
                adapter = ClienteAdapter(clientes)
                recyclerView.adapter = adapter
                recyclerView.visibility = View.VISIBLE // Mostrar RecyclerView si hay datos
            }
        } catch (e: Exception) {
            Log.e("ReportesActivity", "Error al mostrar clientes: ${e.message}")
            Toast.makeText(this, "Error al cargar los clientes: ${e.message}", Toast.LENGTH_SHORT).show()
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
}