package com.example.androidclubdeportivo.model.dao

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.example.androidclubdeportivo.model.Socio

class SociosDAO(private val dbHelper: ClubDatabaseHelper) {


    fun inscribirSocio(idCliente: Int, cuotaFija: Double): Long {
        val db: SQLiteDatabase = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("id_cliente", idCliente)
            put("cuota_fija", cuotaFija)
        }
        return db.insert("Socios", null, values)
    }


    fun obtenerCuotaFija(idCliente: Int): Double {
        val db: SQLiteDatabase = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            "Socios",
            arrayOf("cuota_fija"),
            "id_cliente = ?",
            arrayOf(idCliente.toString()),
            null,
            null,
            null
        )

        return if (cursor.moveToFirst()) {
            cursor.getDouble(cursor.getColumnIndexOrThrow("cuota_fija"))
        } else {
            0.0 // Retornar 0 si no se encuentra el socio
        }.also {
            cursor.close()
        }
    }

    // Función para verificar si un cliente es socio
    fun esSocio(idCliente: Int): Boolean {
        val db: SQLiteDatabase = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            "Socios",
            null,
            "id_cliente = ?",
            arrayOf(idCliente.toString()),
            null,
            null,
            null
        )

        val isSocio = cursor.count > 0
        cursor.close()
        return isSocio
    }
}