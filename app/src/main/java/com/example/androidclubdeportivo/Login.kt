package com.example.androidclubdeportivo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.EditText
import android.widget.CheckBox
//import androidx.navigation.findNavController

class Login : AppCompatActivity() {

    private lateinit var etUser: EditText
    private lateinit var etPassword: EditText
    private lateinit var cbRememberMe: CheckBox
    private lateinit var btnSubscribe: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)
        etUser = findViewById(R.id.etUser)
        etPassword = findViewById(R.id.etPassword)
        cbRememberMe = findViewById(R.id.cbRememberMe)
        btnSubscribe = findViewById(R.id.btnSubscribe)

        // Set click listener for the subscribe button


                val btnSubscribe = findViewById<Button>(R.id.btnSubscribe)
                btnSubscribe.setOnClickListener{
                    if (validateInput()) {
                    val intent= Intent(this, Home::class.java)
                    startActivity(intent)
            } else {
                Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateInput(): Boolean {
        val username = etUser.text.toString().trim()
        val password = etPassword.text.toString().trim()

        return username.isNotEmpty() && password.isNotEmpty()
    }

    }

