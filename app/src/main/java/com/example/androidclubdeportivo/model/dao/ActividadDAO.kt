package com.example.androidclubdeportivo.model.dao

import android.content.ContentValues
import android.database.Cursor
import android.util.Log
import com.example.androidclubdeportivo.model.Profesor
import com.example.androidclubdeportivo.model.HorarioActividad
import com.example.androidclubdeportivo.model.Actividad
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale



class ActividadDAO(private val dbHelper: ClubDatabaseHelper) {

    fun insertarActividad(
        nombre: String,
        cupo: Int,
        precio: Double,
        idProfesor: Int,
        idSede: Int
    ): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("nombre", nombre)
            put("cupo", cupo)
            put("precio", precio)
            put("id_profesor", idProfesor)
            put("id_sede", idSede)
        }
        val id = db.insert("Actividades", null, values)
        Log.d("ActividadDAO", "Insertando actividad: $nombre, ID: $id")
        return id
    }

    fun insertarHorario(
        idActividad: Long,
        diaSemana: String,
        horaInicio: String,
        horaFin: String
    ): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("id_actividad", idActividad)
            put("dia_semana", diaSemana)
            put("hora_inicio", horaInicio)
            put("hora_fin", horaFin)
        }
        val id = db.insert("HorariosActividad", null, values)
        Log.d("ActividadDAO", "Insertando horario para ID de actividad: $idActividad, ID de horario: $id")
        return id
    }

    fun getProfesorByActividad(idActividad: Int): Profesor? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT p.* FROM Profesores p INNER JOIN Actividades a ON p.id_profesor = a.id_profesor WHERE a.id_actividad = ?", arrayOf(idActividad.toString()))

        return if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("id_profesor"))
            val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
            val apellido = cursor.getString(cursor.getColumnIndexOrThrow("apellido"))
            val documento = cursor.getString(cursor.getColumnIndexOrThrow("documento"))
            val telefono = cursor.getString(cursor.getColumnIndexOrThrow("telefono"))
            val email = cursor.getString(cursor.getColumnIndexOrThrow("email"))
            Profesor(id, nombre, apellido, documento, telefono, email)
        } else {
            null
        }.also {
            cursor.close()
        }
    }



    fun getProfesores(): List<Profesor> {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM Profesores", null)
        val profesores = mutableListOf<Profesor>()

        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("id_profesor"))
            val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
            val apellido = cursor.getString(cursor.getColumnIndexOrThrow("apellido"))
            val documento = cursor.getString(cursor.getColumnIndexOrThrow("documento"))
            val telefono = cursor.getString(cursor.getColumnIndexOrThrow("telefono"))
            val email = cursor.getString(cursor.getColumnIndexOrThrow("email"))

            profesores.add(Profesor(id, nombre, apellido, documento, telefono, email))
        }
        cursor.close()
        return profesores
    }



    fun getProfesoresByActividad(idActividad: Int): List<Profesor> {
        val db = dbHelper.readableDatabase
        val query = """
            SELECT DISTINCT p.* 
            FROM Profesores p 
            INNER JOIN Actividades a ON p.id_profesor = a.id_profesor 
            WHERE a.id_actividad = ?
        """
        val cursor = db.rawQuery(query, arrayOf(idActividad.toString()))
        val profesores = mutableListOf<Profesor>()

        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("id_profesor"))
            val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
            val apellido = cursor.getString(cursor.getColumnIndexOrThrow("apellido"))
            val documento = cursor.getString(cursor.getColumnIndexOrThrow("documento"))
            val telefono = cursor.getString(cursor.getColumnIndexOrThrow("telefono"))
            val email = cursor.getString(cursor.getColumnIndexOrThrow("email"))
            profesores.add(Profesor(id, nombre, apellido, documento, telefono, email))
        }
        cursor.close()
        return profesores
    }
    fun getTodasLasActividades(): List<Map<String, Any?>> {
        val db = dbHelper.readableDatabase
        val query = """
        SELECT DISTINCT a.id_actividad, a.nombre
        FROM Actividades a
        ORDER BY a.nombre
        """
        val cursor = db.rawQuery(query, null)
        val actividades = mutableListOf<Map<String, Any?>>()

        while (cursor.moveToNext()) {
            val actividad = mutableMapOf<String, Any?>()
            actividad["id_actividad"] = cursor.getInt(cursor.getColumnIndexOrThrow("id_actividad"))
            actividad["nombre"] = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
            actividades.add(actividad)
        }
        cursor.close()
        return actividades
    }
    fun getSedes(): List<Map<String, Any?>> {
        val db = dbHelper.readableDatabase
        val query = "SELECT id_sede, nombre FROM Sedes ORDER BY nombre"
        val cursor = db.rawQuery(query, null)
        val sedes = mutableListOf<Map<String, Any?>>()

        Log.d("ActividadDAO", "Executing getSedes query: $query")

        while (cursor.moveToNext()) {
            val sede = mutableMapOf<String, Any?>()
            sede["id_sede"] = cursor.getInt(cursor.getColumnIndexOrThrow("id_sede"))
            sede["nombre"] = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
            sedes.add(sede)
        }
        cursor.close()

        Log.d("ActividadDAO", "Retrieved ${sedes.size} sedes")
        return sedes
    }
    fun getHorarios(): List<HorarioActividad> {
        val horarios = mutableListOf<HorarioActividad>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            """
        SELECT h.id_horario AS id, h.id_actividad, h.dia_semana, h.hora_inicio, h.hora_fin, s.nombre AS sede, a.nombre AS actividad
        FROM HorariosActividad h
        JOIN Actividades a ON h.id_actividad = a.id_actividad
        JOIN Sedes s ON a.id_sede = s.id_sede
        """, null
        )

        if (cursor.moveToFirst()) {
            do {
                val horario = HorarioActividad(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    idActividad = cursor.getInt(cursor.getColumnIndexOrThrow("id_actividad")),
                    diaSemana = cursor.getString(cursor.getColumnIndexOrThrow("dia_semana")),
                    horaInicio = cursor.getString(cursor.getColumnIndexOrThrow("hora_inicio")),
                    horaFin = cursor.getString(cursor.getColumnIndexOrThrow("hora_fin")),
                    sede = cursor.getString(cursor.getColumnIndexOrThrow("sede")),
                    actividad = cursor.getString(cursor.getColumnIndexOrThrow("actividad"))
                )
                horarios.add(horario)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return horarios
    }


    fun getActividadesPorSede(idSede: Int): List<Map<String, Any?>> {
        val db = dbHelper.readableDatabase
        val query = """
        SELECT id_actividad, nombre 
        FROM Actividades 
        WHERE id_sede = ? 
        ORDER BY nombre
    """
        val cursor = db.rawQuery(query, arrayOf(idSede.toString()))
        val actividades = mutableListOf<Map<String, Any?>>()

        while (cursor.moveToNext()) {
            val actividad = mutableMapOf<String, Any?>()
            actividad["id_actividad"] = cursor.getInt(cursor.getColumnIndexOrThrow("id_actividad"))
            actividad["nombre"] = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
            actividades.add(actividad)
        }
        cursor.close()
        return actividades
    }
    fun getHorariosPorActividad(idActividad: Int): List<Map<String, Any?>> {
        val db = dbHelper.readableDatabase
        val query = """
        SELECT dia_semana, hora_inicio, hora_fin 
        FROM HorariosActividad 
        WHERE id_actividad = ?
        ORDER BY dia_semana
    """
        val cursor = db.rawQuery(query, arrayOf(idActividad.toString()))
        val horarios = mutableListOf<Map<String, Any?>>()

        while (cursor.moveToNext()) {
            val horario = mutableMapOf<String, Any?>()
            horario["dia_semana"] = cursor.getString(cursor.getColumnIndexOrThrow("dia_semana"))
            horario["hora_inicio"] = cursor.getString(cursor.getColumnIndexOrThrow("hora_inicio"))
            horario["hora_fin"] = cursor.getString(cursor.getColumnIndexOrThrow("hora_fin"))
            horarios.add(horario)
        }
        cursor.close()
        return horarios
    }



    fun getActividadesConHorariosBySede(idSede: Int): List<Map<String, Any?>> {
        val db = dbHelper.readableDatabase
        val query = """
        SELECT a.id_actividad, a.nombre, h.dia_semana, h.hora_inicio, h.hora_fin
        FROM Actividades a
        LEFT JOIN HorariosActividad h ON a.id_actividad = h.id_actividad
        WHERE a.id_sede = ?
    """
        val cursor = db.rawQuery(query, arrayOf(idSede.toString()))
        val actividadesConHorarios = mutableListOf<Map<String, Any?>>()

        val columnCount = cursor.columnCount
        while (cursor.moveToNext()) {
            val actividad = mutableMapOf<String, Any?>()
            for (i in 0 until columnCount) {
                actividad[cursor.getColumnName(i)] = cursor.getString(i)
            }
            actividadesConHorarios.add(actividad)
        }
        cursor.close()
        return actividadesConHorarios
    }

    fun inscribirClienteEnActividad(
        clienteId: Int?,
        idActividad: Int,
        idHorario: Int,
        fecha: String
    ): Long {
        val db = dbHelper.writableDatabase
        db.beginTransaction()
        try {
            val values = ContentValues().apply {
                put("id_cliente", clienteId)
                put("id_actividad", idActividad)
                put("id_horario", idHorario)
                put("fecha", fecha)
            }

            val idInscripcion = db.insert("Inscripciones", null, values)
            Log.d("ActividadDAO", "Inscripción realizada con ID: $idInscripcion")

            if (idInscripcion != -1L && clienteId != null) {
                val sociosDAO = SociosDAO(dbHelper)
                val esSocio = sociosDAO.esSocio(clienteId)

                if (!esSocio) {
                    val costoActividad = obtenerPrecioActividad(idActividad)
                    val fechaVencimiento = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) // Fecha de vencimiento es la fecha actual

                    val cuotaValues = ContentValues().apply {
                        put("id_cliente", clienteId)
                        put("monto", costoActividad)
                        put("fecha_vencimiento", fechaVencimiento)
                        put("estado", "Vencido")
                    }
                    val idCuota = db.insert("Cuotas", null, cuotaValues)
                    if (idCuota == -1L) {
                        throw Exception("Error al registrar cuota para la actividad con ID: $idActividad")
                    }
                }
            }

            db.setTransactionSuccessful()
            return idInscripcion
        } catch (e: Exception) {
            Log.e("ActividadDAO", "Error al inscribir cliente en actividad", e)
            return -1
        } finally {
            db.endTransaction()
        }
    }




    fun insertarCuotaPorActividad(
        idCliente: Long,
        monto: Double,
        fechaVencimiento: String,
        estado: String
    ): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("id_cliente", idCliente)
            put("monto", monto)
            put("fecha_vencimiento", fechaVencimiento)
            put("estado", estado)

            // Establecemos la fecha_pago si el estado es "Al día" o "Pagado"
            if (estado.equals("Al día", ignoreCase = true) || estado.equals("Pagado", ignoreCase = true)) {
                put("fecha_pago", SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()))
            } else {
                putNull("fecha_pago")
            }
        }

        val id = db.insert("Cuotas", null, values)
        if (id != -1L) {
            Log.d("ActividadDAO", "Cuota insertada con éxito. ID: $id")
        } else {
            Log.e("ActividadDAO", "Error al insertar cuota")
        }
        return id
    }


    private fun obtenerPrecioActividad(idActividad: Int): Double {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            "Actividades",
            arrayOf("precio"),
            "id_actividad = ?",
            arrayOf(idActividad.toString()),
            null,
            null,
            null
        )

        return if (cursor.moveToFirst()) {
            cursor.getDouble(cursor.getColumnIndexOrThrow("precio"))
        } else {
            0.0
        }.also {
            cursor.close()
        }
    }


}


