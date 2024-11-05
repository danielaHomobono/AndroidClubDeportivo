package com.example.androidclubdeportivo.controller

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.androidclubdeportivo.R
import com.example.androidclubdeportivo.model.dao.ActividadDAO
import com.example.androidclubdeportivo.model.Profesor
import com.example.androidclubdeportivo.model.dao.ClubDatabaseHelper


class Profesores2 : AppCompatActivity() {
    private lateinit var linearLayoutProfesores: LinearLayout
    private lateinit var actividadDAO: ActividadDAO
    private lateinit var btnHome: ImageButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val databaseHelper = ClubDatabaseHelper(this)
        actividadDAO = ActividadDAO(databaseHelper)
        setContentView(R.layout.activity_profesores2)
        initializeViews()
        loadProfesores()
        setupHomeButton()
    }
    private fun initializeViews() {

        btnHome = findViewById(R.id.homeButton)
        linearLayoutProfesores = findViewById(R.id.linearLayoutProfesores)
    }
    private fun loadProfesores() {
        val profesores = actividadDAO.getProfesores() // Obtén la lista de profesores

        for (profesor in profesores) {
            // Crea un TextView para cada profesor
            val textView = TextView(this).apply {
                text = "${profesor.apellido}, ${profesor.nombre}" // Muestra el nombre y apellido
                textSize = 18f
                setTextColor(resources.getColor(android.R.color.black))
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }


            val actividadTextView = TextView(this).apply {

                textSize = 14f

                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }

            linearLayoutProfesores.addView(textView)
            linearLayoutProfesores.addView(actividadTextView)
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
}


