package com.example.androidclubdeportivo.model.dao

import android.annotation.SuppressLint
import android.content.ContentValues
import android.util.Log
import com.example.androidclubdeportivo.model.Actividad
import com.example.androidclubdeportivo.model.HorarioActividad

class ActividadDAO(private val dbHelper: ClubDatabaseHelper) {

    fun insertarActividad(actividad: Actividad): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("nombre", actividad.nombre)
            put("cupo", actividad.cupo)
            put("precio", actividad.precio)
            put("id_profesor", actividad.idProfesor)
            put("id_sede", actividad.idSede)
        }
        return db.insert("Actividades", null, values)
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
        val actividades = mutableListOf<Map<String, Any?>>()
        val db = dbHelper.readableDatabase
        val query = """
        SELECT a.id_actividad, a.nombre, h.id_horario, h.dia_semana, h.hora_inicio, h.hora_fin 
        FROM Actividades a 
        LEFT JOIN HorariosActividad h ON a.id_actividad = h.id_actividad
        WHERE a.id_sede = ?
        ORDER BY a.nombre, h.dia_semana, h.hora_inicio
    """
        val cursor = db.rawQuery(query, arrayOf(idSede.toString()))

        while (cursor.moveToNext()) {
            val actividad: Map<String, Any?> = mapOf(
                "id_actividad" to cursor.getInt(0),
                "nombre" to cursor.getString(1),
                "id_horario" to if (cursor.isNull(2)) null else cursor.getInt(2),
                "dia_semana" to cursor.getString(3),
                "hora_inicio" to cursor.getString(4),
                "hora_fin" to cursor.getString(5)
            )
            actividades.add(actividad)
        }
        cursor.close()
        Log.d("ActividadDAO", "Actividades con horarios obtenidas: $actividades")
        return actividades
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