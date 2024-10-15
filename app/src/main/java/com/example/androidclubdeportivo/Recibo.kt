package com.example.androidclubdeportivo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageButton
import java.text.SimpleDateFormat
import java.util.*

class Recibo : AppCompatActivity() {

    private lateinit var clientNameTextView: TextView
    private lateinit var memberNumberTextView: TextView
    private lateinit var dniNumberTextView: TextView
    private lateinit var dateTextView: TextView
    private lateinit var activityTextView: TextView
    private lateinit var methodPaymentTextView: TextView
    private lateinit var totalTextView: TextView
    private lateinit var printButton: Button
    private lateinit var homeButton: Button
    private lateinit var btnSubscribe: Button
    private lateinit var btnHome: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recibo)


        initializeViews()
        setupHomeButton()


    }
    private fun initializeViews() {

        btnHome = findViewById(R.id.homeButton)
        btnSubscribe = findViewById(R.id.btnSubscribe)  // Inicializa el botón de pago
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

