package com.example.androidclubdeportivo.model

data class Pago(
    val id_pago: Int? = null,
    val id_inscripcion: Int?,
    val id_cuota: Int,
    val fecha_pago: String,
    val monto_pago: Double
)