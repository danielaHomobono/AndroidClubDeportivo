package com.example.androidclubdeportivo.controller

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androidclubdeportivo.R
import com.example.androidclubdeportivo.model.Cliente

class Reportes2 : AppCompatActivity() {

    private lateinit var headerTitle: TextView
    private lateinit var homeButton: ImageButton
    private lateinit var recyclerView: RecyclerView
   // private lateinit var adapter: ClienteAdapter // Adaptador que crearás

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reportes2) // Asegúrate de que este sea el nombre correcto

        headerTitle = findViewById(R.id.headerTitle)
        homeButton = findViewById(R.id.homeButton)
        recyclerView = findViewById(R.id.recyclerView) // Asegúrate de tener un RecyclerView en tu layout

        // Obtener la lista de clientes del Intent
        val clientes = intent.getSerializableExtra("clientes") as? List<Cliente> ?: emptyList()

        // Configurar el RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
       // adapter = ClienteAdapter(clientes)
       // recyclerView.adapter = adapter

    }

}