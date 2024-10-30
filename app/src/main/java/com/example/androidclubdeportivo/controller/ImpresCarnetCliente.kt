package com.example.androidclubdeportivo.controller

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.*
import com.example.androidclubdeportivo.R
import com.example.androidclubdeportivo.model.Cliente
import com.example.androidclubdeportivo.model.dao.ClienteDAO
import com.example.androidclubdeportivo.model.dao.ClubDatabaseHelper

class ImpresCarnetCliente : AppCompatActivity() {

    private lateinit var btnHome: ImageButton
    private lateinit var btnImprimir: Button
    private lateinit var tvNombre: TextView
    private lateinit var tvMemberNumber: TextView
    private lateinit var tvDNI: TextView
    private lateinit var clienteDAO: ClienteDAO
    private var cliente: Cliente? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_impres_carnet_cliente)

        clienteDAO = ClienteDAO(ClubDatabaseHelper(this))

        initializeViews()
        setupListeners()
        loadClienteData()
    }

    private fun initializeViews() {
        btnHome = findViewById(R.id.homeButton)
        btnImprimir = findViewById(R.id.btnSubscribe)
        tvNombre = findViewById(R.id.clientName)
        tvMemberNumber = findViewById(R.id.memberNumber)
        tvDNI = findViewById(R.id.dniNumber)
    }

    private fun setupListeners() {
        btnHome.setOnClickListener {
            val intent = Intent(this, Home::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }

        btnImprimir.setOnClickListener {
            imprimirCarnet()
        }
    }

    private fun loadClienteData() {
        // Asumimos que el ID del cliente se pasa como extra en el Intent
        val clienteId = intent.getIntExtra("CLIENTE_ID", -1)
        if (clienteId != -1) {
            cliente = clienteDAO.getClienteById(clienteId)
            cliente?.let {
                displayClienteInfo(it)
            } ?: run {
                Toast.makeText(this, "Error al cargar los datos del cliente", Toast.LENGTH_SHORT).show()
                finish()
            }
        } else {
            Toast.makeText(this, "Error: ID de cliente no proporcionado", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun displayClienteInfo(cliente: Cliente) {
        tvNombre.text = "Nombre: ${cliente.nombre} ${cliente.apellido}"
        tvMemberNumber.text = "Número de socio: ${cliente.id_cliente}"
        tvDNI.text = "DNI: ${cliente.documento}"
    }

    private fun imprimirCarnet() {
        cliente?.let {
            // Aquí iría la lógica real para imprimir el carnet
            val mensaje = "Imprimiendo carnet para ${it.nombre} ${it.apellido}"
            Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
        } ?: run {
            Toast.makeText(this, "Error: No se pueden obtener los datos del cliente", Toast.LENGTH_SHORT).show()
        }
    }
}