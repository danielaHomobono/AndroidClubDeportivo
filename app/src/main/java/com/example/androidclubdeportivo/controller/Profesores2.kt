package com.example.androidclubdeportivo.controller

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.example.androidclubdeportivo.R

class Profesores2 : AppCompatActivity() {

    private lateinit var btnHome: ImageButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_profesores2)
        initializeViews()
        setupHomeButton()
    }
    private fun initializeViews() {

        btnHome = findViewById(R.id.homeButton)
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


