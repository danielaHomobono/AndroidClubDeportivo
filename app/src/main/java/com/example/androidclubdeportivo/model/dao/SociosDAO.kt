package com.example.androidclubdeportivo.model.dao

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.example.androidclubdeportivo.model.Socio

class SociosDAO(private val dbHelper: ClubDatabaseHelper) {




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