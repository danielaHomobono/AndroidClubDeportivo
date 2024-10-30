package com.example.androidclubdeportivo.controller

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.util.Patterns
import com.example.androidclubdeportivo.model.dao.ClubDatabaseHelper
import com.example.androidclubdeportivo.R
import com.example.androidclubdeportivo.model.Cliente
import com.example.androidclubdeportivo.model.dao.ClienteDAO

class InscripcionCliente : AppCompatActivity() {

    private lateinit var dbHelper: ClubDatabaseHelper
    private lateinit var clienteDAO: ClienteDAO
    private lateinit var etFirstName: EditText
    private lateinit var etLastName: EditText
    private lateinit var spinnerDocumentType: Spinner
    private lateinit var etDocumentNumber: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPhoneNumber: EditText
    private lateinit var etUser: EditText
    private lateinit var etPassword: EditText
    private lateinit var cbInscribirSocio: CheckBox
    private lateinit var cbPresentoFichaMedica: CheckBox
    private lateinit var btnSubscribe: Button
    private lateinit var btnHome: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inscripcion_cliente)

        dbHelper = ClubDatabaseHelper(this)
        clienteDAO = ClienteDAO(dbHelper)

        initializeViews()
        setupSpinner()
        setupValidations()
        setupHomeButton()
    }

    private fun initializeViews() {
        etFirstName = findViewById(R.id.etFirstName)
        etLastName = findViewById(R.id.etLastName)
        spinnerDocumentType = findViewById(R.id.spinnerDocumentType)
        etDocumentNumber = findViewById(R.id.etDocumentNumber)
        etEmail = findViewById(R.id.etEmail)
        etPhoneNumber = findViewById(R.id.etPhoneNumber)
        etUser = findViewById(R.id.etUser)
        etPassword = findViewById(R.id.etPassword)
        cbInscribirSocio = findViewById(R.id.cbInscribirSocio)
        cbPresentoFichaMedica = findViewById(R.id.cbPresentoFichaMedica)
        btnSubscribe = findViewById(R.id.btnSubscribe)
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

        btnSubscribe.setOnClickListener {
            if (validateForm()) {
                if (!cbPresentoFichaMedica.isChecked) {
                    Toast.makeText(
                        this,
                        "Debe presentar la ficha médica en los próximos días",
                        Toast.LENGTH_LONG
                    ).show()
                }
                val cliente = Cliente(
                    nombre = etFirstName.text.toString().trim(),
                    apellido = etLastName.text.toString().trim(),
                    documento = etDocumentNumber.text.toString().trim(),
                    tipo_documento = spinnerDocumentType.selectedItem.toString(),
                    telefono = etPhoneNumber.text.toString().trim(),
                    email = etEmail.text.toString().trim(),
                    direccion = null,
                    fechaUltimoPago = "01/01/2024" // Asigna un valor adecuado

                )

                try {
                    Log.d("InscripcionCliente", "Intentando insertar cliente")
                    val idCliente = clienteDAO.insertarCliente(cliente)
                    Log.d("InscripcionCliente", "ID del cliente insertado: $idCliente")

                    if (idCliente != -1L) {
                        if (cbInscribirSocio.isChecked) {
                            Log.d("InscripcionCliente", "Intentando inscribir socio")
                            val idSocio = clienteDAO.inscribirSocio(idCliente, 20000.0, "2024-12-31", "Al día") // Asegúrate de pasar el estado y la fecha de vencimiento
                            Log.d("InscripcionCliente", "ID del socio inscrito: $idSocio")
                            if (idSocio == -1L) {
                                throw Exception("Error al registrar socio")
                            }
                        }

                        Log.d("InscripcionCliente", "Intentando insertar usuario")
                        val idUsuario = dbHelper.insertUsuario(cliente.email!!, etPassword.text.toString().trim(), "Cliente", idCliente)
                        Log.d("InscripcionCliente", "ID del usuario insertado: $idUsuario")
                        if (idUsuario != -1L) {
                            Toast.makeText(this, "Cliente y usuario registrados con éxito", Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            throw Exception("Error al crear usuario")
                        }
                    } else {
                        throw Exception("Error al registrar cliente")
                    }
                } catch (e: Exception) {
                    Log.e("InscripcionCliente", "Error en el proceso de registro", e)
                    Toast.makeText(this, e.message ?: "Error desconocido", Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
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

    private fun validateForm(): Boolean {
        var isValid = true

        if (etFirstName.text.toString().trim().isEmpty()) {
            etFirstName.error = "El nombre es requerido"
            isValid = false
        }

        if (etLastName.text.toString().trim().isEmpty()) {
            etLastName.error = "El apellido es requerido"
            isValid = false
        }

        if (etDocumentNumber.text.toString().trim().isEmpty()) {
            etDocumentNumber.error = "El número de documento es requerido"
            isValid = false
        } else if (etDocumentNumber.text.toString().length < 8) {
            etDocumentNumber.error = "El número de documento debe tener al menos 8 dígitos"
            isValid = false
        }

        if (etEmail.text.toString().trim().isNotEmpty() &&
            !Patterns.EMAIL_ADDRESS.matcher(etEmail.text.toString()).matches()
        ) {
            etEmail.error = "Email inválido"
            isValid = false
        } else if (clienteDAO.isEmailRegistered(etEmail.text.toString().trim())) {
            etEmail.error = "El email ya está registrado"
            isValid = false
        }

        if (etPhoneNumber.text.toString().trim().isNotEmpty() &&
            !Patterns.PHONE.matcher(etPhoneNumber.text.toString()).matches()
        ) {
            etPhoneNumber.error = "Número de teléfono inválido"
            isValid = false
        }

        if (etUser.text.toString().trim().isEmpty()) {
            etUser.error = "El nombre de usuario es requerido"
            isValid = false
        }

        if (etPassword.text.toString().trim().length < 6) {
            etPassword.error = "La contraseña debe tener al menos 6 caracteres"
            isValid = false
        }

        return isValid
    }
}