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
    private lateinit var btnHome: Button
    private lateinit var etDocumentNumber: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pagar) // Hace referencia al archivo XML que has mostrado

        initializeViews()
        setupHomeButton()
        setupSpinner()

        // Obtener referencia del ImageButton
        val imageButtonQR = findViewById<ImageButton>(R.id.imageButtonqr)

        // Configurar el OnClickListener para el botón QR
        imageButtonQR.setOnClickListener {
            // URL ficticia de Mercado Pago
            val url = "https://www.mercadopago.com/ficticio-pago"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent) // Abre el navegador con la URL
        }


        }
    private fun initializeViews() {
        spinnerDocumentType = findViewById(R.id.spinnerDocumentType)
        etDocumentNumber = findViewById(R.id.etDocumentNumber)
        btnHome = findViewById(R.id.homeButton)

    }
    //Spinner tipo de documento
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
        // Validación para que el DNI solo acepte números
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

