package com.example.androidclubdeportivo.controller

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageButton
import com.example.androidclubdeportivo.R

class Recibo : AppCompatActivity() {

    private lateinit var clientNameTextView: TextView
    private lateinit var memberNumberTextView: TextView
    private lateinit var dniNumberTextView: TextView
    private lateinit var dateTextView: TextView
    private lateinit var activityTextView: TextView
    private lateinit var methodPaymentTextView: TextView
    private lateinit var totalTextView: TextView
    private lateinit var printButton: Button
    private lateinit var homeButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recibo)

        initializeViews()
        setupHomeButton()
        displayReceiptData()
    }

    private fun initializeViews() {
        clientNameTextView = findViewById(R.id.clientName)
        memberNumberTextView = findViewById(R.id.memberNumber)
        dniNumberTextView = findViewById(R.id.dniNumber)
        dateTextView = findViewById(R.id.date)
        activityTextView = findViewById(R.id.activitys)
        methodPaymentTextView = findViewById(R.id.methodPayment)
        totalTextView = findViewById(R.id.total)
        printButton = findViewById(R.id.btnSubscribe)  // Inicializa el botón de imprimir
        homeButton = findViewById(R.id.homeButton)
    }

    private fun setupHomeButton() {
        homeButton.setOnClickListener {
            val intent = Intent(this, Home::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }
    }

    private fun displayReceiptData() {
        // Obtener los datos del Intent
        val intent = intent
        val clientName = intent.getStringExtra("CLIENT_NAME") ?: "Nombre no disponible"
        val memberNumber = intent.getStringExtra("MEMBER_NUMBER") ?: "Número no disponible"
        val dniNumber = intent.getStringExtra("DNI_NUMBER") ?: "DNI no disponible"
        val date = intent.getStringExtra("DATE") ?: "Fecha no disponible"
        val activity = intent.getStringExtra("ACTIVITY") ?: "Actividad no disponible"
        val methodPayment = intent.getStringExtra("METHOD_PAYMENT") ?: "Método no disponible"
        val total = intent.getDoubleExtra("TOTAL", 0.0)

        // Asignar los datos a los TextViews
        clientNameTextView.text = "Nombre: $clientName"
        memberNumberTextView.text = "Número de socio: $memberNumber"
        dniNumberTextView.text = "DNI: $dniNumber"
        dateTextView.text = "Fecha: $date"
        activityTextView.text = "Actividad: $activity"
        methodPaymentTextView.text = "Método de pago: $methodPayment"
        totalTextView.text = "Total: $$total"
    }
}