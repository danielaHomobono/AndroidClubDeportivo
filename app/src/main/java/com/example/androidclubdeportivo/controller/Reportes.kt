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
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androidclubdeportivo.R
import com.example.androidclubdeportivo.controller.Home
import com.example.androidclubdeportivo.model.Cliente
import com.example.androidclubdeportivo.model.dao.ClienteDAO
import com.example.androidclubdeportivo.model.dao.ClubDatabaseHelper

class Reportes : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ClienteAdapter
    private lateinit var spinnerEstado: Spinner
    private lateinit var btnMostrar: Button
    private lateinit var clienteDAO: ClienteDAO
    private lateinit var btnHome: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reportes)


        btnHome = findViewById(R.id.homeButton)
        recyclerView = findViewById(R.id.recyclerViewClientes)
        spinnerEstado = findViewById(R.id.spinnerEstado)
        btnMostrar = findViewById(R.id.btnMostrar)


        clienteDAO = ClienteDAO(ClubDatabaseHelper(this))
        setupHomeButton()


        recyclerView.layoutManager = LinearLayoutManager(this)


        val estados = arrayOf("Vencido", "Al Día")
        val adapterSpinner =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, estados)
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerEstado.adapter = adapterSpinner


        btnMostrar.setOnClickListener {
            try {
                val estadoSeleccionado = spinnerEstado.selectedItem.toString()
                val clientes: List<Cliente> = if (estadoSeleccionado == "Vencido") {
                    clienteDAO.getClientesConCuotasVencidas()
                } else {
                    clienteDAO.getClientesAlDia()
                }

                Log.d(
                    "ReportesActivity",
                    "Clientes obtenidos: ${clientes.size}"
                )


                adapter = ClienteAdapter(clientes)
                recyclerView.adapter = adapter
                recyclerView.visibility = View.VISIBLE // Muestra el RecyclerView
            } catch (e: Exception) {
                Log.e("ReportesActivity", "Error al mostrar clientes: ${e.message}")
            }
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