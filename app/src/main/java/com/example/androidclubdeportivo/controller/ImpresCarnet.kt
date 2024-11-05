package com.example.androidclubdeportivo.controller

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.*
import android.text.Editable
import android.text.TextWatcher
import com.example.androidclubdeportivo.R
import com.example.androidclubdeportivo.model.Cliente
import com.example.androidclubdeportivo.model.dao.ClienteDAO
import com.example.androidclubdeportivo.model.dao.ClubDatabaseHelper

class ImpresCarnet : AppCompatActivity() {

    private lateinit var btnHome: ImageButton
    private lateinit var btnImprimir: Button
    private lateinit var etSearch: EditText
    private lateinit var tvNombre: TextView
    private lateinit var tvMemberNumber: TextView
    private lateinit var tvDNI: TextView
    private lateinit var clienteDAO: ClienteDAO
    private var cliente: Cliente? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_impres_carnet)

        clienteDAO = ClienteDAO(ClubDatabaseHelper(this))

        initializeViews()
        setupListeners()
    }

    private fun initializeViews() {
        btnHome = findViewById(R.id.homeButton)
        btnImprimir = findViewById(R.id.btnSubscribe)
        etSearch = findViewById(R.id.searchEditText)
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

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchCliente(s.toString())
            }
        })
    }

    private fun searchCliente(query: String) {
        if (query.isNotEmpty()) {
            val clientes = clienteDAO.searchClientesByApellido(query)
            if (clientes.isNotEmpty()) {
                cliente = clientes[0] // Tomamos el primer cliente encontrado
                displayClienteInfo(cliente!!)
            } else {
                clearClienteInfo()
            }
        } else {
            clearClienteInfo()
        }
    }

    private fun displayClienteInfo(cliente: Cliente) {
        tvNombre.text = "Nombre: ${cliente.nombre} ${cliente.apellido}"
        tvMemberNumber.text = "Número de socio: ${cliente.id_cliente}"
        tvDNI.text = "DNI: ${cliente.documento}"
    }

    private fun clearClienteInfo() {
        tvNombre.text = "Nombre: "
        tvMemberNumber.text = "Número de socio: "
        tvDNI.text = "DNI: "
        cliente = null
    }

    private fun imprimirCarnet() {
        cliente?.let {
            // Aquí iría la lógica real para imprimir el carnet
            val mensaje = "Imprimiendo carnet para ${it.nombre} ${it.apellido}"
            Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
        } ?: run {
            Toast.makeText(this, "Por favor, busque un cliente antes de imprimir", Toast.LENGTH_SHORT).show()
        }
    }
}