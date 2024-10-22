package com.example.androidclubdeportivo.model.dao

import android.content.ContentValues
import com.example.androidclubdeportivo.model.Cliente


class ClienteDAO(private val dbHelper: ClubDatabaseHelper) {

        // Función para insertar un cliente
        fun insertarCliente(cliente: Cliente): Long {
            val db = dbHelper.writableDatabase
            val values = ContentValues().apply {
                put("nombre", cliente.nombre)
                put("apellido", cliente.apellido)
                put("documento", cliente.documento)
                put("tipo_documento", cliente.tipo_documento)
                put("telefono", cliente.telefono)
                put("email", cliente.email)
                put("direccion", cliente.direccion)
            }
            return db.insert("Clientes", null, values)
        }

        // Función para inscribir un socio (utilizando el id_cliente)
        fun inscribirSocio(id_cliente: Long, cuotaFija: Double): Long {
            val db = dbHelper.writableDatabase
            val values = ContentValues().apply {
                put("id_cliente", id_cliente)
                put("cuota_fija", cuotaFija)
            }
            return db.insert("Socios", null, values)
        }

        // Función para verificar si un email ya está registrado
        fun isEmailRegistered(email: String): Boolean {
            val db = dbHelper.readableDatabase
            val cursor = db.rawQuery("SELECT * FROM Clientes WHERE email = ?", arrayOf(email))
            val exists = cursor.count > 0
            cursor.close()
            return exists
        }
    }

