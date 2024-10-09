package com.example.androidclubdeportivo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.util.Patterns
import android.widget.ImageButton


class Horarios : AppCompatActivity() {

    private lateinit var btnMonday: Button
    private lateinit var btnHome: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_horarios)

        initializateViews()
        setupHomeButton()

    }
    private fun initializateViews(){

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