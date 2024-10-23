package com.example.androidclubdeportivo.controller

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.util.Patterns
import android.widget.AutoCompleteTextView
import android.widget.ImageButton
import com.example.androidclubdeportivo.R
import com.example.androidclubdeportivo.model.dao.ClubDatabaseHelper
import com.example.androidclubdeportivo.model.Cliente
import com.example.androidclubdeportivo.model.dao.ClienteDAO


class ModificacionCliente : AppCompatActivity() {

    private lateinit var autoCompleteClientes: AutoCompleteTextView
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
    private lateinit var btnModification: Button
    private lateinit var btnHome: ImageButton

    private var clienteId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modificacion_cliente)


        // Elimina o comenta estas líneas si no las necesitas
        // clienteId = intent.getIntExtra("CLIENTE_ID", -1)
        // if (clienteId == -1) {
        //     Toast.makeText(this, "Error: No se proporcionó ID de cliente", Toast.LENGTH_LONG).show()
        //     finish()
        //     return
        // }


        dbHelper = ClubDatabaseHelper(this)
        clienteDAO = ClienteDAO(dbHelper)

        initializeViews()
        setupSpinner()
        setupValidations()
        setupHomeButton()
        setupClienteAutoComplete()
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
        btnModification = findViewById(R.id.btnModification)
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
    private fun setupClienteAutoComplete() {
        autoCompleteClientes = findViewById(R.id.autoCompleteClientes)
        val clientes = clienteDAO.getAllClientes()

        if (clientes.isEmpty()) {
            Toast.makeText(this, "No hay clientes registrados", Toast.LENGTH_SHORT).show()
            return
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, clientes.map { "${it.nombre} ${it.apellido} - ${it.documento}" })
        autoCompleteClientes.setAdapter(adapter)

        autoCompleteClientes.setOnItemClickListener { _, _, position, _ ->
            val selectedCliente = clientes[position]
            clienteId = selectedCliente.id_cliente ?: -1
            if (clienteId != -1) {
                loadClienteData(selectedCliente)
            } else {
                Toast.makeText(this, "Error al cargar el cliente seleccionado", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun loadClienteData(cliente: Cliente) {
        etFirstName.setText(cliente.nombre)
        etLastName.setText(cliente.apellido)
        etDocumentNumber.setText(cliente.documento)
        val tipoDocumentoPosition =
            (spinnerDocumentType.adapter as ArrayAdapter<String>).getPosition(cliente.tipo_documento)
        spinnerDocumentType.setSelection(tipoDocumentoPosition)
        etPhoneNumber.setText(cliente.telefono)
        etEmail.setText(cliente.email)
        // Aquí cargarías los datos de usuario y contraseña si los tienes almacenados
        // También puedes manejar los CheckBox si tienes esa información
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


        btnModification.setOnClickListener {
            if (validateForm()) {
                val clienteActualizado = Cliente(
                    id_cliente = clienteId,
                    nombre = etFirstName.text.toString().trim(),
                    apellido = etLastName.text.toString().trim(),
                    documento = etDocumentNumber.text.toString().trim(),
                    tipo_documento = spinnerDocumentType.selectedItem.toString(),
                    telefono = etPhoneNumber.text.toString().trim(),
                    email = etEmail.text.toString().trim(),
                    direccion = null  // Asumiendo que no tienes un campo para dirección en este formulario
                )

                if (clienteDAO.actualizarCliente(clienteActualizado)) {
                    // Aquí deberías manejar la actualización de usuario y contraseña si es necesario
                    Toast.makeText(this, "Cliente actualizado con éxito", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Error al actualizar el cliente", Toast.LENGTH_SHORT).show()
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

        if (etEmail.text.toString().trim().isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(etEmail.text.toString()).matches()) {
            etEmail.error = "Email inválido"
            isValid = false
        }

        if (etPhoneNumber.text.toString().trim().isNotEmpty() && !Patterns.PHONE.matcher(etPhoneNumber.text.toString()).matches()) {
            etPhoneNumber.error = "Número de teléfono inválido"
            isValid = false
        }

        // Aquí puedes agregar validaciones para usuario y contraseña si es necesario

        return isValid
    }
}