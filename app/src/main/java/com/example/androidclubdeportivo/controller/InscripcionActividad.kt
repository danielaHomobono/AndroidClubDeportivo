package com.example.androidclubdeportivo.controller

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.androidclubdeportivo.R

class InscripcionActividad : AppCompatActivity() {

    private lateinit var etDocumentNumber: EditText
    private lateinit var spinnerDocumentType: Spinner
    private lateinit var spinnerSede: Spinner
    private lateinit var spinnerActivity: Spinner
    private lateinit var btnActivitySubscribe: Button
    private lateinit var btnHome: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inscripcion_actividad)


        initializeViews()
        setupHomeButton()
        setupSpinner()
        setupValidations()
    }


    private fun initializeViews() {
         spinnerDocumentType = findViewById(R.id.spinnerDocumentType)
        spinnerSede = findViewById(R.id.spinnerSede)
        spinnerActivity = findViewById(R.id.spinnerActivity)
        etDocumentNumber = findViewById(R.id.etDocumentNumber)
        btnActivitySubscribe = findViewById(R.id.btnActivitySubscribe)
        btnHome = findViewById(R.id.homeButton)
    }


    private fun setupSpinner() {

        val adapterDocumentType = ArrayAdapter.createFromResource(
            this,
            R.array.document_type_array,
            android.R.layout.simple_spinner_item
        )
        adapterDocumentType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDocumentType.adapter = adapterDocumentType

        spinnerDocumentType.setSelection(0)

        // Spinner Sede (Sede corregida)
        val adapterSede = ArrayAdapter.createFromResource(
            this,
            R.array.sede,
            android.R.layout.simple_spinner_item
        )
        adapterSede.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSede.adapter = adapterSede

        // Establecer la opción predeterminada "Sede"
        spinnerSede.setSelection(0)  // Asegura que "Sede" aparezca al inicio

        val adapterActivity = ArrayAdapter.createFromResource(
            this,
            R.array.actividad_array,
            android.R.layout.simple_spinner_item
        )
        adapterActivity.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerActivity.adapter = adapterActivity
    }


    private fun setupValidations() {
        // Validación para que el número de documento solo acepte números
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


        btnActivitySubscribe.setOnClickListener {
            if (validateForm()) {
                // Lógica
                Toast.makeText(this, "Formulario válido, procesando...", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun validateForm(): Boolean {
        var isValid = true

        // Validación del número de documento
        if (etDocumentNumber.text.toString().trim().isEmpty()) {
            etDocumentNumber.error = "El número de documento es requerido"
            isValid = false
        } else if (etDocumentNumber.text.toString().length < 8) {
            etDocumentNumber.error = "El número de documento debe tener al menos 8 dígitos"
            isValid = false
        }

        return isValid
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
