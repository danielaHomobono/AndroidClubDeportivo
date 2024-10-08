package com.example.androidclubdeportivo

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.util.Patterns

class Horarios : AppCompatActivity() {

    private lateinit var btnMonday: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_horarios)

        initializateViews()

    }
    private fun initializateViews(){
        btnMonday=findViewById(R.id.btnMonday)
    }
}