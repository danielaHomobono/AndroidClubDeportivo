package com.example.androidclubdeportivo.model.dao

import android.content.ContentValues
import android.util.Log
import com.example.androidclubdeportivo.model.HorarioActividad
import com.example.androidclubdeportivo.model.Profesor

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
            Profesor(id, nombre, apellido, documento, telefono)
        } else {
            null
        }.also {
            cursor.close()
        }
    }
    fun insertarProfesor(nombre: String, apellido: String, documento: String, telefono: String?, email: String?): Long {
        val db = dbHelper.writableDatabase
        val contentValues = ContentValues().apply {
            put("nombre", nombre)
            put("apellido", apellido)
            put("documento", documento)
            put("telefono", telefono)
            put("email", email)
        }
        return db.insert("Profesores", null, contentValues)
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

            profesores.add(Profesor(id, nombre, apellido, documento, telefono))
        }
        cursor.close()
        return profesores
    }

    fun insertarHorarioActividad(horario: HorarioActividad): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("id_actividad", horario.idActividad)
            put("dia_semana", horario.diaSemana)
            put("hora_inicio", horario.horaInicio)
            put("hora_fin", horario.horaFin)
        }
        return db.insert("HorariosActividad", null, values)
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

        // Imprimir nombres de columnas para depuración
        val columnCount = cursor.columnCount
        for (i in 0 until columnCount) {
            Log.d("CursorColumn", "Column $i: ${cursor.getColumnName(i)}")
        }

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
        val values = ContentValues().apply {
            put("id_cliente", clienteId)
            put("id_actividad", idActividad)
            put("id_horario", idHorario)
            put("fecha", fecha)
        }
        return try {
            val id = db.insert("Inscripciones", null, values)
            Log.d("ActividadDAO", "Inscripción realizada con ID: $id")
            id
        } catch (e: Exception) {
            Log.e("ActividadDAO", "Error al inscribir cliente en actividad", e)
            -1
        }
    }

    fun insertarDatosDePrueba() {
        val db = dbHelper.writableDatabase

        // Primero, limpiamos las tablas
        db.delete("Actividades", null, null)
        db.delete("HorariosActividad", null, null)

        // Lista de actividades deseadas
        val actividades = listOf(
            "Tenis", "Yoga", "Pilates", "Spinning", "Zumba", "Boxeo", "Basquet"
        )

        // Insertamos las actividades
        for (actividad in actividades) {
            val values = ContentValues().apply {
                put("nombre", actividad)
                put("cupo", 20)
                put("precio", 1000.0)
                put("id_profesor", 1)  // Asumiendo que tienes al menos un profesor
                put("id_sede", 1)  // Asumiendo que tienes al menos una sede
            }
            val idActividad = db.insert("Actividades", null, values)

            // Insertamos dos horarios de ejemplo para cada actividad
            val dias = listOf("Lunes", "Miércoles")
            for (dia in dias) {
                val horarioValues = ContentValues().apply {
                    put("id_actividad", idActividad)
                    put("dia_semana", dia)
                    put("hora_inicio", "10:00")
                    put("hora_fin", "11:00")
                }
                db.insert("HorariosActividad", null, horarioValues)
            }
        }

        Log.d("ActividadDAO", "Actividades y horarios de prueba insertados")
    }
}