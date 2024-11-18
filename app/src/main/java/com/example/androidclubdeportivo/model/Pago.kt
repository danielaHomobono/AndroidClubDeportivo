package com.example.androidclubdeportivo.model

data class Pago(

    val id_pago: Int = 0,
    val id_cliente: Int,
    val id_cuota: Int,
    val id_inscripcion: Int?,
    val fecha_pago: String,
    val monto_pago: Double
)
    /*//val id_pago: Int? = null,
    val id_pago: Int = 0,
    val id_cliente: Int,
    val id_cuota: Int,
    val id_inscripcion: Int?,
    val fecha_pago: String,
    val monto_pago: Double
)*/
// Cuota.kt
data class Cuota(
    val idCuota: Int,
    val idCliente: Long,
    val monto: Double,
    val fechaVencimiento: String,
    val estado: String,
    val fechaPago: String? = null
)
