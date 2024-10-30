package com.example.androidclubdeportivo.controller

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androidclubdeportivo.R
import com.example.androidclubdeportivo.model.HorarioActividad
import com.example.androidclubdeportivo.model.dao.ActividadDAO
import com.example.androidclubdeportivo.model.dao.ClubDatabaseHelper

class Horarios : AppCompatActivity() {

    private lateinit var btnHome: ImageButton
    private lateinit var spinnerSede: Spinner
    private lateinit var spinnerActivity: Spinner
    private lateinit var recyclerViewHorarios: RecyclerView
    private lateinit var radioGroupFiltro: RadioGroup
    private lateinit var actividadDAO: ActividadDAO
    private lateinit var allHorarios: List<HorarioActividad>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_horarios)
        val databaseHelper = ClubDatabaseHelper(this)
        actividadDAO = ActividadDAO(databaseHelper)

        initializeViews()
        setupHomeButton()
        loadData()
        setupSpinners()
        setupRecyclerView()
        setupRadioGroup()
        loadHorarios()
    }

    private fun initializeViews() {
        spinnerSede = findViewById(R.id.spinnerSede)
        btnHome = findViewById(R.id.homeButton)
        spinnerActivity = findViewById(R.id.spinnerActivity)
        recyclerViewHorarios = findViewById(R.id.recyclerViewHorarios)
        radioGroupFiltro = findViewById(R.id.radioGroupFiltro)
    }

    private fun loadData() {
        // Cargar todas las sedes y actividades para llenar los spinners
        val sedes = actividadDAO.getSedes()
        val sedeNames = listOf("Todas") + sedes.map { it["nombre"].toString() }
        val actividades = actividadDAO.getTodasLasActividades()
        val actividadNames = listOf("Todas") + actividades.map { it["nombre"].toString() }

        // Configurar adaptadores para los spinners
        spinnerSede.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, sedeNames)
        spinnerActivity.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, actividadNames)

        // Obtener todos los horarios disponibles
        allHorarios = actividadDAO.getHorarios()
    }

    private fun setupSpinners() {
        spinnerSede.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                if (radioGroupFiltro.checkedRadioButtonId == R.id.radioButtonSede) {
                    filterHorarios()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        spinnerActivity.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                if (radioGroupFiltro.checkedRadioButtonId == R.id.radioButtonActividad) {
                    filterHorarios()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun setupRadioGroup() {
        radioGroupFiltro.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioButtonSede -> {
                    spinnerSede.isEnabled = true
                    spinnerActivity.isEnabled = false
                }
                R.id.radioButtonActividad -> {
                    spinnerSede.isEnabled = false
                    spinnerActivity.isEnabled = true
                }
            }
            filterHorarios()
        }
        radioGroupFiltro.check(R.id.radioButtonSede)
        spinnerSede.isEnabled = true
        spinnerActivity.isEnabled = false
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
        recyclerViewHorarios.layoutManager = LinearLayoutManager(this)
        updateRecyclerView(allHorarios)
    }

    private fun loadHorarios() {
        updateRecyclerView(allHorarios)
    }

    private fun filterHorarios() {
        val filteredHorarios = when (radioGroupFiltro.checkedRadioButtonId) {
            R.id.radioButtonSede -> {
                val selectedSede = spinnerSede.selectedItem.toString()
                if (selectedSede == "Todas") allHorarios else allHorarios.filter { it.sede == selectedSede }
            }
            R.id.radioButtonActividad -> {
                val selectedActividad = spinnerActivity.selectedItem.toString()
                if (selectedActividad == "Todas") allHorarios else allHorarios.filter { it.actividad == selectedActividad }
            }
            else -> allHorarios
        }
        updateRecyclerView(filteredHorarios)
    }

    private fun updateRecyclerView(horarios: List<HorarioActividad>) {
        recyclerViewHorarios.adapter = HorariosAdapter(horarios)
    }
}

class HorariosAdapter(private val horarios: List<HorarioActividad>) :
    RecyclerView.Adapter<HorariosAdapter.HorarioViewHolder>() {

    class HorarioViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val sedeTextView: TextView = view.findViewById(R.id.textViewSede)
        val actividadTextView: TextView = view.findViewById(R.id.textViewActividad)
        val horariosTextView: TextView = view.findViewById(R.id.textViewHorario)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HorarioViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_horarios, parent, false)
        return HorarioViewHolder(view)
    }

    override fun onBindViewHolder(holder: HorarioViewHolder, position: Int) {
        val horario = horarios[position]
        holder.sedeTextView.text = horario.sede // Usa `horario.sede` directamente
        holder.actividadTextView.text = horario.actividad // Usa `horario.actividad` directamente
        holder.horariosTextView.text = "Hora: ${horario.horaInicio} - ${horario.horaFin}"
    }

    override fun getItemCount() = horarios.size
}

