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


        // Insertar usuario de prueba solo si no existe
        if (!authService.isUserRegistered("dani")) {
            val isRegistered = authService.registerUser("dani", "1234", "Admin")
            if (isRegistered) {
                Log.d(TAG, "Usuario de prueba registrado con éxito.")
                Toast.makeText(this, "Usuario de prueba registrado con éxito", Toast.LENGTH_SHORT).show()
            } else {
                Log.e(TAG, "Error al registrar usuario de prueba")
                Toast.makeText(this, "Error al registrar usuario de prueba", Toast.LENGTH_SHORT).show()
            }
        } else {
            Log.d(TAG, "El usuario de prueba ya existe")
        }

        // Imprimir el contenido de la tabla Usuarios
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
                Log.d(TAG, "Intentando iniciar sesión con: $username, password: $password")

                if (authService.isUserRegistered(username)) {
                    Log.d(TAG, "El usuario $username está registrado")
                    if (authService.login(username, password)) {
                        Log.d(TAG, "Inicio de sesión exitoso para: $username")
                        Toast.makeText(this, "¡Inicio de sesión exitoso!", Toast.LENGTH_SHORT).show()
                        if (cbRememberMe.isChecked) {
                            authService.rememberUser(username)
                        }
                        val intent = Intent(this, Home::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Log.d(TAG, "Contraseña incorrecta para: $username")
                        Toast.makeText(this, "Contraseña incorrecta.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.d(TAG, "El usuario $username no está registrado")
                    Toast.makeText(this, "Usuario no registrado.", Toast.LENGTH_SHORT).show()
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

        // Cargar usuario recordado si existe
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