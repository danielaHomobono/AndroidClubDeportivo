package com.example.androidclubdeportivo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.util.Patterns
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.Spinner


class Horarios : AppCompatActivity() {


    private lateinit var btnHome: ImageButton
    private lateinit var spinnerSede: Spinner
    private lateinit var spinnerActivity: Spinner
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_horarios)

        initializateViews()
        setupHomeButton()
        setupSpinnerSede()
        setupSpinnerActivity()
    }
    private fun initializateViews(){
        spinnerSede= findViewById(R.id.spinnerSede)
        btnHome = findViewById(R.id.homeButton)
        spinnerActivity = findViewById(R.id.spinnerActivity)
    }
    private fun setupHomeButton() {
        btnHome.setOnClickListener {
            val intent = Intent(this, Home::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }

    }
    private fun setupSpinnerSede() {
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.sede,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSede.adapter = adapter
    }
    private fun setupSpinnerActivity() {
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.actividad_array,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerActivity.adapter = adapter
    }

}