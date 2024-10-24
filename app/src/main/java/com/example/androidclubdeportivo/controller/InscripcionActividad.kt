package com.example.androidclubdeportivo.controller

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.androidclubdeportivo.R
import com.example.androidclubdeportivo.model.dao.ActividadDAO
import com.example.androidclubdeportivo.model.dao.ClubDatabaseHelper

import com.example.androidclubdeportivo.model.dao.ClienteDAO
import java.text.SimpleDateFormat
import java.util.*

class InscripcionActividad : AppCompatActivity() {

    private lateinit var dbHelper: ClubDatabaseHelper
    private lateinit var actividadDao: ActividadDAO
    private lateinit var clienteDAO: ClienteDAO
    private lateinit var etDocumentNumber: EditText
    private lateinit var spinnerDocumentType: Spinner
    private lateinit var spinnerSede: Spinner
    private lateinit var spinnerActivity: Spinner
    private lateinit var spinnerHorario: Spinner
    private lateinit var btnActivitySubscribe: Button
    private lateinit var btnHome: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inscripcion_actividad)

        dbHelper = ClubDatabaseHelper(this)
        actividadDao = ActividadDAO(dbHelper)
        clienteDAO = ClienteDAO(dbHelper)

        initializeViews()
        setupHomeButton()

        // Llama a insertTestSedes() solo una vez
        val prefs = getSharedPreferences("MiAppPrefs", MODE_PRIVATE)
        val sedesInsertadas = prefs.getBoolean("sedes_insertadas", false)
        if (!sedesInsertadas) {
            dbHelper.insertTestSedes()
            prefs.edit().putBoolean("sedes_insertadas", true).apply()
        }

        setupSpinners()
        setupValidations()
        insertarDatosDePruebaUnaVez()
        printActividadesYHorarios()
    }

    private fun initializeViews() {
        spinnerDocumentType = findViewById(R.id.spinnerDocumentType)
        spinnerSede = findViewById(R.id.spinnerSede)
        spinnerActivity = findViewById(R.id.spinnerActivity)
        spinnerHorario = findViewById(R.id.spinnerHorario)
        etDocumentNumber = findViewById(R.id.etDocumentNumber)
        btnActivitySubscribe = findViewById(R.id.btnActivitySubscribe)
        btnHome = findViewById(R.id.homeButton)
    }

    private fun setupSpinners() {
        val adapterDocumentType = ArrayAdapter.createFromResource(
            this,
            R.array.document_type_array,
            android.R.layout.simple_spinner_item
        )
        adapterDocumentType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDocumentType.adapter = adapterDocumentType
        // Configuración del spinner de sedes
        val sedes = dbHelper.getAllSedes()
        val adapterSede = ArrayAdapter(this, android.R.layout.simple_spinner_item, sedes)
        adapterSede.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSede.adapter = adapterSede

        spinnerSede.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val idSede = position + 1
                val actividadesConHorarios = actividadDao.getActividadesConHorariosBySede(idSede)
                setupActivitySpinner(actividadesConHorarios)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun setupActivitySpinner(actividadesConHorarios: List<Map<String, Any?>>) {
        val actividadesUnicas = actividadesConHorarios.map { it["nombre"] as String }.distinct()
        val adapterActivity = ArrayAdapter(this, android.R.layout.simple_spinner_item, actividadesUnicas)
        adapterActivity.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerActivity.adapter = adapterActivity

        spinnerActivity.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val actividadSeleccionada = parent.getItemAtPosition(position).toString()
                setupHorarioSpinner(actividadesConHorarios, actividadSeleccionada)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun setupHorarioSpinner(actividadesConHorarios: List<Map<String, Any?>>, actividadSeleccionada: String) {
        val horariosActividad = actividadesConHorarios.filter { it["nombre"] == actividadSeleccionada }
        val horarios = horariosActividad.mapNotNull {
            val diaSemana = it["dia_semana"] as? String
            val horaInicio = it["hora_inicio"] as? String
            val horaFin = it["hora_fin"] as? String
            if (diaSemana != null && horaInicio != null && horaFin != null) {
                "$diaSemana $horaInicio - $horaFin"
            } else {
                null
            }
        }.distinct()

        val adapterHorario = if (horarios.isEmpty()) {
            ArrayAdapter(this, android.R.layout.simple_spinner_item, listOf("Sin horarios disponibles"))
        } else {
            ArrayAdapter(this, android.R.layout.simple_spinner_item, horarios)
        }
        adapterHorario.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerHorario.adapter = adapterHorario
    }

    private fun insertarDatosDePruebaUnaVez() {
        val prefs = getSharedPreferences("MiAppPrefs", Context.MODE_PRIVATE)
        val datosInsertados = prefs.getBoolean("datos_prueba_insertados", false)

        if (!datosInsertados) {
            dbHelper.insertTestSedes()  // Asegúrate de que esto se llame aquí
            actividadDao.insertarDatosDePrueba()
            prefs.edit().putBoolean("datos_prueba_insertados", true).apply()
        }
    }


    private fun setupValidations() {
        etDocumentNumber.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.toString().isNotEmpty() && !s.toString().matches(Regex("^[0-9]+$"))) {
                    etDocumentNumber.error = "Solo se permiten números"
                    etDocumentNumber.setText(s.toString().replace(Regex("[^0-9]"), ""))
                    etDocumentNumber.setSelection(etDocumentNumber.text.length)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        btnActivitySubscribe.setOnClickListener {
            if (validateForm()) {
                inscribirEnActividad()
            }
        }
    }

    private fun validateForm(): Boolean {
        var isValid = true

        if (etDocumentNumber.text.toString().trim().isEmpty()) {
            etDocumentNumber.error = "El número de documento es requerido"
            isValid = false
        } else if (etDocumentNumber.text.toString().length < 8) {
            etDocumentNumber.error = "El número de documento debe tener al menos 8 dígitos"
            isValid = false
        }

        return isValid
    }
    fun printActividadesYHorarios() {
        val db = dbHelper.readableDatabase
        val cursorActividades = db.rawQuery("SELECT * FROM Actividades", null)
        Log.d("ActividadDAO", "Contenido de la tabla Actividades:")
        while (cursorActividades.moveToNext()) {
            val id = cursorActividades.getInt(cursorActividades.getColumnIndexOrThrow("id_actividad"))
            val nombre = cursorActividades.getInt(cursorActividades.getColumnIndexOrThrow("nombre"))
            Log.d("ActividadDAO", "ID: $id, Nombre: $nombre")
        }
        cursorActividades.close()

        val cursorHorarios = db.rawQuery("SELECT * FROM HorariosActividad", null)
        Log.d("ActividadDAO", "Contenido de la tabla HorariosActividad:")
        while (cursorHorarios.moveToNext()) {
            val id = cursorHorarios.getInt(cursorHorarios.getColumnIndexOrThrow("id_horario"))
            val idActividad = cursorHorarios.getInt(cursorHorarios.getColumnIndexOrThrow("id_actividad"))
            val diaSemana = cursorHorarios.getString(cursorHorarios.getColumnIndexOrThrow("dia_semana"))
            val horaInicio = cursorHorarios.getString(cursorHorarios.getColumnIndexOrThrow("hora_inicio"))
            val horaFin = cursorHorarios.getString(cursorHorarios.getColumnIndexOrThrow("hora_fin"))
            Log.d("ActividadDAO", "ID: $id, ID Actividad: $idActividad, Día: $diaSemana, Hora: $horaInicio - $horaFin")
        }
        cursorHorarios.close()
    }

    private fun inscribirEnActividad() {
        try {
            Log.d("InscripcionActividad", "Iniciando inscripción")

            val tipoDocumento = spinnerDocumentType.selectedItem?.toString()
            Log.d("InscripcionActividad", "Tipo de documento seleccionado: $tipoDocumento")

            val numeroDocumento = etDocumentNumber.text.toString()
            Log.d("InscripcionActividad", "Número de documento: $numeroDocumento")

            val clienteId = clienteDAO.getClienteByDocumento(tipoDocumento.toString(), numeroDocumento)
            Log.d("InscripcionActividad", "ID del cliente: $clienteId")

            val actividadSeleccionada = spinnerActivity.selectedItem?.toString()
            Log.d("InscripcionActividad", "Actividad seleccionada: $actividadSeleccionada")

            val horarioSeleccionado = spinnerHorario.selectedItemPosition
            Log.d("InscripcionActividad", "Posición del horario seleccionado: $horarioSeleccionado")

            val fechaActual = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            Log.d("InscripcionActividad", "Fecha actual: $fechaActual")

            if (clienteId != null) {
                val actividadesConHorarios = actividadDao.getActividadesConHorariosBySede(spinnerSede.selectedItemPosition + 1)
                Log.d("InscripcionActividad", "Actividades con horarios: $actividadesConHorarios")

                val actividadHorario = actividadesConHorarios.filter { it["nombre"] == actividadSeleccionada }.getOrNull(horarioSeleccionado)
                Log.d("InscripcionActividad", "Actividad y horario seleccionados: $actividadHorario")

                if (actividadHorario != null) {
                    val idActividad = actividadHorario["id_actividad"] as? Int
                    val idHorario = actividadHorario["id_horario"] as? Int
                    Log.d("InscripcionActividad", "ID de actividad: $idActividad, ID de horario: $idHorario")

                    if (idActividad != null && idHorario != null) {
                        val inscripcionId = actividadDao.inscribirClienteEnActividad(clienteId, idActividad, idHorario, fechaActual)
                        Log.d("InscripcionActividad", "ID de inscripción: $inscripcionId")

                        if (inscripcionId != -1L) {
                            Toast.makeText(this, "Inscripción realizada con éxito", Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            Toast.makeText(this, "Error al realizar la inscripción", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Log.e("InscripcionActividad", "ID de actividad o horario es nulo")
                        Toast.makeText(this, "Error: Actividad o horario no válido", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e("InscripcionActividad", "No se encontró la actividad y horario seleccionados")
                    Toast.makeText(this, "Error: Actividad o horario no encontrado", Toast.LENGTH_SHORT).show()
                }
            } else {
                Log.e("InscripcionActividad", "Cliente no encontrado")
                Toast.makeText(this, "Cliente no encontrado", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.e("InscripcionActividad", "Error al inscribir en actividad", e)
            Toast.makeText(this, "Error al inscribir: ${e.message}", Toast.LENGTH_LONG).show()
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