package com.example.androidclubdeportivo

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Spinner
import android.text.Editable
import android.widget.Button
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ArrayAdapter
import android.util.Patterns
import android.widget.ImageButton
import android.widget.Toast

class InscripcionActividad : AppCompatActivity() {
    private lateinit var etDocumentNumber: EditText
    private lateinit var spinnerSede: Spinner
    private lateinit var spinnerActivity: Spinner
    private lateinit var btnActivitySubscribe: Button
    private lateinit var btnHome: ImageButton
    private lateinit var spinnerDocumentType: Spinner


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inscripcion_actividad)

        initializeViews()
        setupHomeButton()
        setupSpinner()
        setupValidations()
        setupSpinnerSede()
        setupSpinnerActivity()
    }
    private fun initializeViews() {
        spinnerSede = findViewById(R.id.spinnerSede)
        spinnerActivity= findViewById(R.id.spinnerActivity)
        etDocumentNumber = findViewById(R.id.etDocumentNumber)
        btnActivitySubscribe = findViewById(R.id.btnActivitySubscribe)
        btnHome = findViewById(R.id.homeButton)

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

        btnActivitySubscribe.setOnClickListener {
            if (validateForm()) {
                // Aquí iría la lógica para procesar el formulario
                Toast.makeText(this, "Formulario válido, procesando...", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun validateForm(): Boolean {
        var isValid = true


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
    private fun setupSpinnerSede() {
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.sede,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSede.adapter = adapter
    }

    private fun setupSpinnerActivity() {
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.actividad_array,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerActivity.adapter = adapter
    }
}




