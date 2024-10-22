package com.example.androidclubdeportivo.controller

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.example.androidclubdeportivo.R

class Reportes2 : AppCompatActivity() {
    private lateinit var btnHome: ImageButton



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_reportes2)

        btnHome = findViewById(R.id.homeButton)

        setupHomeButton()

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