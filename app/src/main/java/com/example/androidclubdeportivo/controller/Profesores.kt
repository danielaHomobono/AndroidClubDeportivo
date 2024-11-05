package com.example.androidclubdeportivo.controller

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androidclubdeportivo.R
import com.example.androidclubdeportivo.model.dao.ActividadDAO
import com.example.androidclubdeportivo.model.dao.ClubDatabaseHelper
import com.example.androidclubdeportivo.model.Profesor

class Profesores : AppCompatActivity() {

    private lateinit var spinnerApellido: Spinner
    private lateinit var spinnerActividad: Spinner
    private lateinit var btnHome: ImageButton
    private lateinit var recyclerViewProfesores: RecyclerView
    private lateinit var radioGroupFiltro: RadioGroup
    private lateinit var actividadDAO: ActividadDAO
    private lateinit var allProfesores: List<Profesor>
    private lateinit var allActividades: List<Map<String, Any?>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profesores)

        val databaseHelper = ClubDatabaseHelper(this)
        actividadDAO = ActividadDAO(databaseHelper)
        initializeViews()
        setupHomeButton()
        loadData()
        setupSpinners()
        setupRadioGroup()
        setupRecyclerView()
        loadProfesores()
    }

    private fun initializeViews() {
        spinnerApellido = findViewById(R.id.spinnerApellido)
        spinnerActividad = findViewById(R.id.spinnerActividad)
        btnHome = findViewById(R.id.homeButton)
        recyclerViewProfesores = findViewById(R.id.recyclerViewProfesores)
        radioGroupFiltro = findViewById(R.id.radioGroupFiltro)
    }

    private fun loadData() {
        allProfesores = actividadDAO.getProfesores()
        allActividades = actividadDAO.getTodasLasActividades()
    }

    private fun setupSpinners() {

        val apellidos = listOf("Todos") + allProfesores.map { it.apellido }.distinct().sorted()
        val apellidoAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, apellidos)
        apellidoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerApellido.adapter = apellidoAdapter


        val actividades = listOf("Todas") + allActividades.map { it["nombre"] as String }.distinct().sorted()
        val actividadAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, actividades)
        actividadAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerActividad.adapter = actividadAdapter

        spinnerApellido.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                if (radioGroupFiltro.checkedRadioButtonId == R.id.radioButtonNombre) {
                    filterProfesores()
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        spinnerActividad.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                if (radioGroupFiltro.checkedRadioButtonId == R.id.radioButtonActividad) {
                    filterProfesores()
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun setupRadioGroup() {
        radioGroupFiltro.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioButtonNombre -> {
                    spinnerApellido.isEnabled = true
                    spinnerActividad.isEnabled = false
                }
                R.id.radioButtonActividad -> {
                    spinnerApellido.isEnabled = false
                    spinnerActividad.isEnabled = true
                }
            }
            filterProfesores()
        }


        radioGroupFiltro.check(R.id.radioButtonNombre)
        spinnerApellido.isEnabled = true
        spinnerActividad.isEnabled = false
    }

    private fun setupHomeButton() {
        btnHome.setOnClickListener {
            val intent = Intent(this, Home::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }
    }

    private fun setupRecyclerView() {
        recyclerViewProfesores.layoutManager = LinearLayoutManager(this)
    }

    private fun loadProfesores() {
        updateRecyclerView(allProfesores)
    }

    private fun filterProfesores() {
        val filteredProfesores = when (radioGroupFiltro.checkedRadioButtonId) {
            R.id.radioButtonNombre -> {
                val selectedApellido = spinnerApellido.selectedItem.toString()
                if (selectedApellido == "Todos") {
                    allProfesores
                } else {
                    allProfesores.filter { it.apellido == selectedApellido }
                }
            }
            R.id.radioButtonActividad -> {
                val selectedActividad = spinnerActividad.selectedItem.toString()
                if (selectedActividad == "Todas") {
                    allProfesores
                } else {
                    val actividadId = allActividades.find { it["nombre"] == selectedActividad }?.get("id_actividad") as? Int
                    actividadId?.let { id ->
                        actividadDAO.getProfesoresByActividad(id)
                    } ?: emptyList()
                }
            }
            else -> allProfesores
        }

        updateRecyclerView(filteredProfesores)
    }

    private fun updateRecyclerView(profesores: List<Profesor>) {
        val adapter = ProfesoresAdapter(profesores)
        recyclerViewProfesores.adapter = adapter
    }
}

class ProfesoresAdapter(private val profesores: List<Profesor>) :
    RecyclerView.Adapter<ProfesoresAdapter.ProfesorViewHolder>() {

    class ProfesorViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nombreTextView: TextView = view.findViewById(R.id.textViewNombreProfesor)
        val apellidoTextView: TextView = view.findViewById(R.id.textViewApellidoProfesor)
        val documentoTextView: TextView = view.findViewById(R.id.textViewDocumentoProfesor)
        val telefonoTextView: TextView = view.findViewById(R.id.textViewTelefonoProfesor)
        val emailTextView: TextView = view.findViewById(R.id.textViewEmailProfesor)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfesorViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_profesor, parent, false)
        return ProfesorViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProfesorViewHolder, position: Int) {
        val profesor = profesores[position]
        holder.nombreTextView.text = profesor.nombre
        holder.apellidoTextView.text = profesor.apellido
        holder.documentoTextView.text = "Doc: ${profesor.documento}"
        holder.telefonoTextView.text = "Tel: ${profesor.telefono ?: "N/A"}"
        holder.emailTextView.text = "Email: ${profesor.email ?: "N/A"}"
    }

    override fun getItemCount() = profesores.size
}