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
    private lateinit var textViewNombre: TextView
    private lateinit var textViewSede: TextView
    private lateinit var textViewClase: TextView
    private lateinit var textViewHorario: TextView
    private lateinit var textViewProfesor: TextView

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

        // Llama a insertarDatosDePrueba() solo una vez
        val datosInsertados = prefs.getBoolean("datos_prueba_insertados", false)
        if (!datosInsertados) {
            insertarDatosDePrueba()  // Llama al método de inserción
            prefs.edit().putBoolean("datos_prueba_insertados", true).apply()
        }

        setupSpinners()
        setupValidations()

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
        textViewNombre = findViewById(R.id.textView)
        textViewSede = findViewById(R.id.textView4)
        textViewClase = findViewById(R.id.textView5)
        textViewHorario = findViewById(R.id.textView6)
        textViewProfesor = findViewById(R.id.textView7)
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
        // Configuración del spinner de sedes
        // Configuración del spinner de sedes
        val sedes = actividadDao.getSedes() // Llama al método getSedes en actividadDAO
        val nombresSedes =
            sedes.map { it["nombre"].toString() } // Extrae solo los nombres para el spinner
        val adapterSede = ArrayAdapter(this, android.R.layout.simple_spinner_item, nombresSedes)
        adapterSede.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSede.adapter = adapterSede

        spinnerSede.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                // Obtener la sede seleccionada desde la lista `sedes` usando `position`
                val sedeSeleccionada = sedes[position]
                val sedeNombre = sedeSeleccionada["nombre"].toString()
                val idSede = sedeSeleccionada["id_sede"] as Int

                // Actualiza el TextView con el nombre de la sede seleccionada
                textViewSede.text = sedeNombre

                // Cargar las actividades con horarios usando `idSede`
                val actividadesConHorarios = actividadDao.getActividadesConHorariosBySede(idSede)
                setupActivitySpinner(actividadesConHorarios)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

        private fun setupActivitySpinner(actividadesConHorarios: List<Map<String, Any?>>) {
        // Mapea nombres de actividades
        val actividadesUnicas = actividadesConHorarios.mapNotNull { it["nombre"] as? String }.distinct()

        val adapterActivity = ArrayAdapter(this, android.R.layout.simple_spinner_item, actividadesUnicas)
        adapterActivity.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerActivity.adapter = adapterActivity

        spinnerActivity.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val actividadSeleccionada = parent.getItemAtPosition(position) as? String ?: return
                textViewClase.text = actividadSeleccionada

                // Obtiene el ID de la actividad, asegurándote de que sea del tipo correcto
                val idActividad = obtenerIdActividad(actividadSeleccionada) ?: -1

                if (idActividad != -1) {
                    val profesor = actividadDao.getProfesorByActividad(idActividad)
                    textViewProfesor.text = profesor?.nombre ?: "Sin profesor asignado"
                } else {
                    textViewProfesor.text = "Actividad no válida"
                    Log.d("SpinnerActivity", "Actividades disponibles: $actividadesUnicas")
                }

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

        val adapterHorario = ArrayAdapter(this, android.R.layout.simple_spinner_item, horarios.ifEmpty { listOf("Sin horarios disponibles") })
        adapterHorario.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerHorario.adapter = adapterHorario

        spinnerHorario.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val horarioSeleccionado = parent.getItemAtPosition(position) as? String ?: "Sin horarios disponibles"
                textViewHorario.text = horarioSeleccionado
                Log.d("SpinnerHorario", "Horarios disponibles para la actividad: $horarios")
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
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

    private fun insertarDatosDePrueba() {
        // Inserta una nueva actividad con id_sede 2
        val idActividad = actividadDao.insertarActividad("Boxeo", 20, 1000.0, 1, 2)

        // Inserta horarios para la nueva actividad
        if (idActividad != -1L) {
            actividadDao.insertarHorario(idActividad, "Lunes", "10:00", "11:00")
            actividadDao.insertarHorario(idActividad, "Miércoles", "10:00", "11:00")
            Log.d("ActividadDAO", "Actividad y horarios insertados correctamente")
        } else {
            Log.e("ActividadDAO", "Error al insertar la actividad")
        }
    }

    private fun setupValidations() {
        etDocumentNumber.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // Solo proceder si hay texto en el campo
                if (s.toString().isNotEmpty()) {
                    val tipoDocumento = spinnerDocumentType.selectedItem?.toString()
                    val numeroDocumento = s.toString()

                    // Obtener el cliente usando la nueva función
                    val cliente = clienteDAO.obtenerClientePorDocumento(tipoDocumento.toString(), numeroDocumento)

                    // Actualizar el TextView con el nombre y apellido del cliente
                    textViewNombre.text = if (cliente != null) {
                        "${cliente.nombre} ${cliente.apellido}" // Concatenar nombre y apellido
                    } else {
                        "Cliente no encontrado"
                    }
                } else {
                    // Si el campo está vacío, limpiar el TextView
                    textViewNombre.text = ""
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
            val nombre = cursorActividades.getString(cursorActividades.getColumnIndexOrThrow("nombre"))
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

            // Usa la nueva función para obtener el cliente completo
            val cliente = clienteDAO.obtenerClientePorDocumento(tipoDocumento.toString(), numeroDocumento)
            Log.d("InscripcionActividad", "Cliente encontrado: ${cliente?.nombre} ${cliente?.apellido}")

            // Actualiza el TextView con el nombre del cliente
            textViewNombre.text = cliente?.nombre ?: "Cliente no encontrado"

            val actividadSeleccionada = spinnerActivity.selectedItem?.toString()
            Log.d("InscripcionActividad", "Actividad seleccionada: $actividadSeleccionada")

            val horarioSeleccionado = spinnerHorario.selectedItem?.toString()
            Log.d("InscripcionActividad", "Horario seleccionado: $horarioSeleccionado")

            val fechaActual = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            Log.d("InscripcionActividad", "Fecha actual: $fechaActual")

            if (cliente != null) {
                // Obtén el ID de la actividad y el horario usando los métodos que agregaste
                val idActividad = obtenerIdActividad(actividadSeleccionada ?: "")
                val idHorario = obtenerIdHorario(horarioSeleccionado ?: "")

                Log.d("InscripcionActividad", "ID de actividad: $idActividad, ID de horario: $idHorario")

                if (idActividad != null && idHorario != null) {
                    val inscripcionId = actividadDao.inscribirClienteEnActividad(cliente.id_cliente, idActividad, idHorario, fechaActual)
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


    private fun obtenerIdActividad(nombreActividad: String): Int? {
        return when (nombreActividad) {
            "Tenis" -> 1
            "Yoga" -> 2
            "Pilates" -> 3
            "Spinning" -> 4
            "Zumba" -> 5
            "Boxeo" -> 6
            "Basquet" -> 7
            "Natación" -> 10
            else -> null
        }
    }

    private fun obtenerIdHorario(horarioSeleccionado: String): Int? {
        return when (horarioSeleccionado) {

            "Lunes 10:00 - 11:00" -> 15 // ID correcto
            "Miércoles 10:00 - 11:00" -> 14
            "Martes 10:00 - 11:00" -> 17
            "Jueves 10:00 - 11:00" -> 18
            "Lunes 11:00 - 12:00" -> 19
            "Miércoles 11:00 - 12:00" -> 20
            "Viernes 11:00 - 12:00" -> 21
            "Martes 09:00 - 10:00" -> 22
            // Agrega más horarios según sea necesario
            else -> null

        }
        Log.d("InscripcionActividad", "Horario seleccionado para obtener ID: $horarioSeleccionado")
    }
}

