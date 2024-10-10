package com.example.androidclubdeportivo

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class InscripcionActividad : AppCompatActivity() {

    private lateinit var etDocumentNumber: EditText
    private lateinit var spinnerSede: Spinner
    private lateinit var spinnerActivity: Spinner
    private lateinit var btnActivitySubscribe: Button
    private lateinit var btnHome: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inscripcion_actividad)

        // Inicialización de las vistas
        initializeViews()

        // Configuración del botón "Home"
        setupHomeButton()

        // Configuración de los Spinners
        setupSpinner()

        // Configuración de validaciones
        setupValidations()
    }

    // Inicializa las vistas de la actividad
    private fun initializeViews() {
        spinnerSede = findViewById(R.id.spinnerSede)
        spinnerActivity = findViewById(R.id.spinnerActivity)
        etDocumentNumber = findViewById(R.id.etDocumentNumber)
        btnActivitySubscribe = findViewById(R.id.btnActivitySubscribe)
        btnHome = findViewById(R.id.homeButton)
    }

    // Configura el Spinner con los datos correspondientes
    private fun setupSpinner() {
        // Adaptador para el spinner de sede
        val adapterSede = ArrayAdapter.createFromResource(
            this,
            R.array.sede, // Reemplazar con el array de sede
            android.R.layout.simple_spinner_item
        )
        adapterSede.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSede.adapter = adapterSede

        // Adaptador para el spinner de actividad
        val adapterActivity = ArrayAdapter.createFromResource(
            this,
            R.array.actividad_array, // Reemplazar con el array de actividad
            android.R.layout.simple_spinner_item
        )
        adapterActivity.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerActivity.adapter = adapterActivity
    }

    // Configura las validaciones para el formulario
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

        // Configura la validación y acción del botón de inscripción
        btnActivitySubscribe.setOnClickListener {
            if (validateForm()) {
                // Lógica para procesar el formulario cuando es válido
                Toast.makeText(this, "Formulario válido, procesando...", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Valida los campos del formulario
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

    // Configura el botón "Home" para ir a la pantalla principal
    private fun setupHomeButton() {
        btnHome.setOnClickListener {
            val intent = Intent(this, Home::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }
    }
}
