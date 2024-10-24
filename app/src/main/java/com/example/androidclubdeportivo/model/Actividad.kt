package com.example.androidclubdeportivo.model

data class Actividad(
    val id: Int? = null,
    val nombre: String,
    val cupo: Int,
    val precio: Double,
    val idProfesor: Int,
    val idSede: Int
)

data class HorarioActividad(
    val id: Int? = null,
    val idActividad: Int,
    val diaSemana: String,
    val horaInicio: String,
    val horaFin: String
)



