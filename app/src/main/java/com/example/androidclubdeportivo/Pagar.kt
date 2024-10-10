package com.example.androidclubdeportivo



import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity



class Pagar : AppCompatActivity() {

    private lateinit var spinnerDocumentType: Spinner
    private lateinit var btnHome: ImageButton
    private lateinit var etDocumentNumber: EditText
    private lateinit var btnSubscribe: Button  // Botón de pago

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pagar)
        initializeViews()
        setupHomeButton()
        setupSpinner()
        setupValidations()

        //  para el QR
        val imageButtonQR = findViewById<ImageButton>(R.id.imageButtonQr)
        imageButtonQR.setOnClickListener {
            val url = "https://www.mercadopago.com/ficticio-pago"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }


        btnSubscribe.setOnClickListener {
            if (etDocumentNumber.text.isNotEmpty()) {

                Toast.makeText(this, "Pago registrado con éxito", Toast.LENGTH_LONG).show()
                val intent = Intent(this, Recibo::class.java)
                startActivity(intent)
            } else {
                etDocumentNumber.error = "Por favor ingrese el número de documento"
            }
        }
    }

    private fun initializeViews() {
        spinnerDocumentType = findViewById(R.id.spinnerDocumentType)
        etDocumentNumber = findViewById(R.id.etDocumentNumber)
        btnHome = findViewById(R.id.homeButton)
        btnSubscribe = findViewById(R.id.btnSubscribe)
    }

    private fun setupSpinner() {
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.document_type_array,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDocumentType.adapter = adapter
    }

    private fun setupHomeButton() {
        btnHome.setOnClickListener {
            val intent = Intent(this, Home::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }
    }

    private fun setupValidations() {
        etDocumentNumber.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.toString().isNotEmpty() && !s.toString().matches(Regex("^[0-9]+$"))) {
                    etDocumentNumber.error = "Solo se permiten números"
                    etDocumentNumber.setText(s.toString().replace(Regex("[^0-9]"), ""))
                    etDocumentNumber.setSelection(etDocumentNumber.text.length)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }
}
