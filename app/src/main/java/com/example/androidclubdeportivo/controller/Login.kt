package com.example.androidclubdeportivo.controller

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.androidclubdeportivo.model.dao.ClubDatabaseHelper
import com.example.androidclubdeportivo.R

class Login : AppCompatActivity() {

    private lateinit var etUser: EditText
    private lateinit var etPassword: EditText
    private lateinit var cbRememberMe: CheckBox
    private lateinit var btnSubscribe: Button
    private lateinit var btnClearUser: ImageButton
    private lateinit var btnTogglePassword: ImageButton
    private lateinit var authService: AutenService

    companion object {
        private const val TAG = "Login"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etUser = findViewById(R.id.etUser)
        etPassword = findViewById(R.id.etPassword)
        cbRememberMe = findViewById(R.id.cbRememberMe)
        btnSubscribe = findViewById(R.id.btnSubscribe)
        btnClearUser = findViewById(R.id.btnClearUser)
        btnTogglePassword = findViewById(R.id.btnTogglePassword)

        authService = AutenService(this)

        val admins = listOf("Dani", "Vero", "Roma", "Emi")
        for (admin in admins) {
            if (!authService.isUserRegistered(admin)) {
                val isRegistered = authService.registerUser(admin, "1234", "Admin")
                if (isRegistered) {
                    Log.d(TAG, "Usuario de prueba $admin registrado con éxito.")
                    Toast.makeText(this, "Usuario de prueba $admin registrado con éxito", Toast.LENGTH_SHORT).show()
                } else {
                    Log.e(TAG, "Error al registrar usuario de prueba $admin")
                    Toast.makeText(this, "Error al registrar usuario de prueba $admin", Toast.LENGTH_SHORT).show()
                }
            } else {
                Log.d(TAG, "El usuario de prueba $admin ya existe")
            }
        }

        val dbHelper = ClubDatabaseHelper(this)
        dbHelper.printUsersTable()

        val sucursalCerro = findViewById<LinearLayout>(R.id.sucursal_cerro)
        sucursalCerro.setOnClickListener {
            sendEmail("sucursal.cerro@example.com")
        }

        val sucursalCordoba = findViewById<LinearLayout>(R.id.sucursal_cordoba)
        sucursalCordoba.setOnClickListener {
            sendEmail("sucursal.cordoba@example.com")
        }

        btnSubscribe.setOnClickListener {
            if (validateInput()) {
                val username = etUser.text.toString().trim()
                val password = etPassword.text.toString().trim()
                Log.d(TAG, "Intentando iniciar sesión con: $username")

                val (userType, clienteId) = authService.login(username, password)
                if (userType != null) {
                    Log.d(TAG, "Inicio de sesión exitoso para $username como $userType")
                    Toast.makeText(this, "¡Inicio de sesión exitoso!", Toast.LENGTH_SHORT).show()

                    if (cbRememberMe.isChecked) {
                        authService.rememberUser(username)
                    }

                    when (userType) {
                        "Admin" -> {
                            val intent = Intent(this, Home::class.java)
                            intent.putExtra("USERNAME", username)
                            startActivity(intent)
                        }
                        "Socio" -> {
                            if (clienteId != null) {
                                val intent = Intent(this, ImpresCarnetCliente::class.java)
                                intent.putExtra("CLIENTE_ID", clienteId)
                                intent.putExtra("USERNAME", username)
                                startActivity(intent)
                            } else {
                                Log.e(TAG, "ID de cliente no encontrado para el socio: $username")
                                Toast.makeText(this, "Error: ID de socio no encontrado", Toast.LENGTH_SHORT).show()
                            }
                        }
                        else -> {
                            Log.e(TAG, "Tipo de usuario no permitido: $userType")
                            Toast.makeText(this, "No tienes permiso para acceder", Toast.LENGTH_SHORT).show()
                        }
                    }
                    finish()
                } else {
                    Toast.makeText(this, "Credenciales incorrectas o usuario no autorizado.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        btnClearUser.setOnClickListener {
            etUser.text.clear()
        }

        btnTogglePassword.setOnClickListener {
            if (etPassword.transformationMethod == PasswordTransformationMethod.getInstance()) {
                etPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                btnTogglePassword.setImageResource(R.drawable.close_eye)
            } else {
                etPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                btnTogglePassword.setImageResource(R.drawable.ojo)
            }
            etPassword.setSelection(etPassword.text.length)
        }

        val rememberedUser = authService.getRememberedUser()
        if (rememberedUser != null) {
            etUser.setText(rememberedUser)
            cbRememberMe.isChecked = true
        }
    }

    private fun validateInput(): Boolean {
        val username = etUser.text.toString().trim()
        val password = etPassword.text.toString().trim()
        return username.isNotEmpty() && password.isNotEmpty()
    }

    private fun sendEmail(email: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            putExtra(Intent.EXTRA_SUBJECT, "Consulta a la sucursal")
            putExtra(Intent.EXTRA_TEXT, "Hola, me gustaría hacer una consulta.")
        }

        try {
            startActivity(Intent.createChooser(intent, "Enviar email..."))
        } catch (ex: android.content.ActivityNotFoundException) {
            Toast.makeText(this, "No tienes clientes de correo instalados.", Toast.LENGTH_SHORT).show()
        }
    }
}