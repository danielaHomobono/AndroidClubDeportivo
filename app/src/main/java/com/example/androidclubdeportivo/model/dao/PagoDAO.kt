package com.example.androidclubdeportivo.model.dao



import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.example.androidclubdeportivo.model.Pago

class PagoDAO(private val dbHelper: ClubDatabaseHelper) {

    // Función para insertar un nuevo pago
    fun insertarPago(pago: Pago): Long {
        val db: SQLiteDatabase = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("id_inscripcion", pago.id_inscripcion)
            put("id_cuota", pago.id_cuota)
            put("fecha_pago", pago.fecha_pago)
            put("monto_pago", pago.monto_pago)
        }
        return db.insert("Pagos", null, values)
    }

    // Función para obtener todos los pagos (opcional)
    fun getAllPagos(): List<Pago> {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query("Pagos", null, null, null, null, null, null)
        val pagosList = mutableListOf<Pago>()

        while (cursor.moveToNext()) {
            val id_pago = cursor.getInt(cursor.getColumnIndexOrThrow("id_pago"))
            val id_inscripcion = cursor.getInt(cursor.getColumnIndexOrThrow("id_inscripcion"))
            val id_cuota = cursor.getInt(cursor.getColumnIndexOrThrow("id_cuota"))
            val fecha_pago = cursor.getString(cursor.getColumnIndexOrThrow("fecha_pago"))
            val monto_pago = cursor.getDouble(cursor.getColumnIndexOrThrow("monto_pago"))

            pagosList.add(Pago(id_pago, id_inscripcion, id_cuota, fecha_pago, monto_pago))
        }
        cursor.close()
        return pagosList
    }

    // Función para obtener pagos por id_inscripcion (opcional)
    fun getPagosByInscripcion(idInscripcion: Int): List<Pago> {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            "Pagos",
            null,
            "id_inscripcion = ?",
            arrayOf(idInscripcion.toString()),
            null,
            null,
            null
        )
        val pagosList = mutableListOf<Pago>()

        while (cursor.moveToNext()) {
            val id_pago = cursor.getInt(cursor.getColumnIndexOrThrow("id_pago"))
            val id_cuota = cursor.getInt(cursor.getColumnIndexOrThrow("id_cuota"))
            val fecha_pago = cursor.getString(cursor.getColumnIndexOrThrow("fecha_pago"))
            val monto_pago = cursor.getDouble(cursor.getColumnIndexOrThrow("monto_pago"))

            pagosList.add(Pago(id_pago, idInscripcion, id_cuota, fecha_pago, monto_pago))
        }
        cursor.close()
        return pagosList
    }
}