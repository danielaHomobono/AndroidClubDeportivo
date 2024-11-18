package com.example.androidclubdeportivo.model.dao



import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.example.androidclubdeportivo.model.Cuota


import com.example.androidclubdeportivo.model.Pago

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PagoDAO(private val dbHelper: ClubDatabaseHelper) {
    fun insertarPago(pago: Pago, tipoCuota: TipoCuota, esSocio: Boolean = false): Long {
        val db: SQLiteDatabase = dbHelper.writableDatabase
        db.beginTransaction()
        try {
            Log.d("PagoDAO", "Intentando insertar pago para el cliente: ${pago.id_cliente}")

            // Calcular la nueva fecha de vencimiento
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val fechaPago = sdf.parse(pago.fecha_pago)
            val calendar = Calendar.getInstance()
            calendar.time = fechaPago

            when (tipoCuota) {
                TipoCuota.DIARIA -> calendar.add(Calendar.DAY_OF_MONTH, 1)
                TipoCuota.MENSUAL -> calendar.add(Calendar.MONTH, 1)
            }

            val nuevaFechaVencimiento = sdf.format(calendar.time)

            // Obtener todas las cuotas vencidas del cliente
            val cuotasVencidas = obtenerCuotasVencidas(pago.id_cliente)
            var montoRestante = pago.monto_pago
            var pagoInsertado = false

            for (cuota in cuotasVencidas) {
                // Calcular el monto a pagar para esta cuota
                val montoPagado = when {
                    tipoCuota == TipoCuota.DIARIA || esSocio -> cuota.monto
                    else -> minOf(montoRestante, cuota.monto)
                }

                // Actualizar el estado de la cuota
                val cuotaValues = ContentValues().apply {
                    put("estado", "Al día")
                    put("fecha_pago", pago.fecha_pago)
                    put("fecha_vencimiento", nuevaFechaVencimiento)
                    put("monto", maxOf(0.0, cuota.monto - montoPagado))
                }
                db.update("Cuotas", cuotaValues, "id_cuota = ?", arrayOf(cuota.idCuota.toString()))

                // Insertar el pago en  Pagos
                val pagoValues = ContentValues().apply {
                    put("id_inscripcion", pago.id_inscripcion)
                    put("id_cuota", cuota.idCuota)
                    put("fecha_pago", pago.fecha_pago)
                    put("monto_pago", montoPagado)
                }
                val idPago = db.insert("Pagos", null, pagoValues)

                if (idPago != -1L) {
                    pagoInsertado = true
                    Log.d(
                        "PagoDAO",
                        "Pago insertado para la cuota ${cuota.idCuota} con id: $idPago"
                    )
                } else {
                    Log.e("PagoDAO", "Error al insertar pago para la cuota ${cuota.idCuota}")
                }

                montoRestante -= montoPagado

                // Si no es cuota diaria y no es socio, y ya no queda monto por pagar, salimos del bucle
                if (tipoCuota != TipoCuota.DIARIA && !esSocio && montoRestante <= 0) {
                    break
                }
            }

            db.setTransactionSuccessful()
            Log.d("PagoDAO", "Transacción completada con éxito")
            return if (pagoInsertado) 1L else -1L
        } catch (e: Exception) {
            Log.e("PagoDAO", "Error al insertar pago: ${e.message}")
            return -1
        } finally {
            db.endTransaction()
            Log.d("PagoDAO", "Transacción finalizada")
        }
    }


    private fun obtenerCuotasVencidas(idCliente: Int): List<Cuota> {
        val db = dbHelper.readableDatabase
        val cuotas = mutableListOf<Cuota>()
        val query = """
        SELECT id_cuota, id_cliente, monto, fecha_vencimiento, estado, fecha_pago
        FROM Cuotas 
        WHERE id_cliente = ? AND estado = 'Vencido'
        ORDER BY fecha_vencimiento ASC
    """
        val cursor = db.rawQuery(query, arrayOf(idCliente.toString()))

        while (cursor.moveToNext()) {
            val idCuota = cursor.getInt(cursor.getColumnIndexOrThrow("id_cuota"))
            val idClienteCuota = cursor.getLong(cursor.getColumnIndexOrThrow("id_cliente"))
            val monto = cursor.getDouble(cursor.getColumnIndexOrThrow("monto"))
            val fechaVencimiento =
                cursor.getString(cursor.getColumnIndexOrThrow("fecha_vencimiento"))
            val estado = cursor.getString(cursor.getColumnIndexOrThrow("estado"))
            val fechaPago = cursor.getString(cursor.getColumnIndexOrThrow("fecha_pago"))

            cuotas.add(Cuota(idCuota, idClienteCuota, monto, fechaVencimiento, estado, fechaPago))
        }
        cursor.close()
        return cuotas
    }


    private fun verificarCuotaExiste(idCuota: Int): Boolean {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            "Cuotas",
            arrayOf("id_cuota"),
            "id_cuota = ?",
            arrayOf(idCuota.toString()),
            null,
            null,
            null
        )
        val existe = cursor.count > 0
        cursor.close()
        return existe
    }

    fun obtenerIdCuotaActual(idCliente: Int): Int {
        val db = dbHelper.readableDatabase
        val query = """
        SELECT id_cuota FROM Cuotas 
        WHERE id_cliente = ? AND estado = 'Vencido' 
        ORDER BY fecha_vencimiento ASC 
        LIMIT 1
    """
        val cursor = db.rawQuery(query, arrayOf(idCliente.toString()))

        return if (cursor.moveToFirst()) {
            val idCuota = cursor.getInt(cursor.getColumnIndexOrThrow("id_cuota"))
            cursor.close()
            idCuota
        } else {
            cursor.close()
            -1 // Retorna -1 si no se encuentra una cuota vencida
        }
    }


    fun verificarEstadoCuota(idCuota: Int): String {
        val db = dbHelper.readableDatabase
        val query = "SELECT estado, fecha_pago, fecha_vencimiento FROM Cuotas WHERE id_cuota = ?"
        val cursor = db.rawQuery(query, arrayOf(idCuota.toString()))

        return if (cursor.moveToFirst()) {
            val estado = cursor.getString(cursor.getColumnIndexOrThrow("estado"))
            val fechaPago = cursor.getString(cursor.getColumnIndexOrThrow("fecha_pago"))
            val fechaVencimiento =
                cursor.getString(cursor.getColumnIndexOrThrow("fecha_vencimiento"))
            cursor.close()
            "Estado: $estado, Fecha de pago: $fechaPago, Fecha de vencimiento: $fechaVencimiento"
        } else {
            cursor.close()
            "Cuota no encontrada"
        }
    }


    fun actualizarEstadoYFechaCuota(
        idCuota: Int,
        nuevoEstado: String,
        nuevaFechaVencimiento: String
    ): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("estado", nuevoEstado)
            put("fecha_vencimiento", nuevaFechaVencimiento)
        }
        val rowsAffected = db.update("Cuotas", values, "id = ?", arrayOf(idCuota.toString()))
        return rowsAffected > 0
    }


    fun obtenerFechaVencimientoCuota(idCuota: Int): String? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            "Cuotas",
            arrayOf("fecha_vencimiento"),
            "id_cuota = ?",
            arrayOf(idCuota.toString()),
            null,
            null,
            null
        )

        return if (cursor.moveToFirst()) {
            cursor.getString(cursor.getColumnIndexOrThrow("fecha_vencimiento"))
        } else {
            null // Devuelve null si no se encuentra la cuota
        }.also {
            cursor.close()
        }
    }


    fun getAllPagos(): List<Pago> {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query("Pagos", null, null, null, null, null, null)
        val pagosList = mutableListOf<Pago>()

        while (cursor.moveToNext()) {
            val id_pago = cursor.getInt(cursor.getColumnIndexOrThrow("id_pago"))
            val id_cliente = cursor.getInt(cursor.getColumnIndexOrThrow("id_cliente"))
            val id_cuota = cursor.getInt(cursor.getColumnIndexOrThrow("id_cuota"))
            val id_inscripcion =
                if (cursor.isNull(cursor.getColumnIndexOrThrow("id_inscripcion"))) {
                    null
                } else {
                    cursor.getInt(cursor.getColumnIndexOrThrow("id_inscripcion"))
                }
            val fecha_pago = cursor.getString(cursor.getColumnIndexOrThrow("fecha_pago"))
            val monto_pago = cursor.getDouble(cursor.getColumnIndexOrThrow("monto_pago"))

            pagosList.add(
                Pago(
                    id_pago,
                    id_cliente,
                    id_cuota,
                    id_inscripcion,
                    fecha_pago,
                    monto_pago
                )
            )
        }
        cursor.close()
        return pagosList
    }

}











