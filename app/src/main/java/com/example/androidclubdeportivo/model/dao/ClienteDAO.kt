package com.example.androidclubdeportivo.model.dao

import android.content.ContentValues
import android.database.Cursor
import androidx.core.database.getIntOrNull
import androidx.core.database.getStringOrNull
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
    fun searchClientes(query: String): List<Cliente> {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("""
        SELECT * FROM Clientes
        WHERE id_cliente IN (
            SELECT id_cliente FROM ClientesFTS
            WHERE ClientesFTS MATCH ?
        )
    """, arrayOf(query))

        val clientes = mutableListOf<Cliente>()
        while (cursor.moveToNext()) {
            clientes.add(cursorToCliente(cursor))
        }
        cursor.close()

        return clientes
    }
    fun getAllClientes(): List<Cliente> {
        val clientes = mutableListOf<Cliente>()
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            "Clientes",
            null,
            null,
            null,
            null,
            null,
            "nombre ASC"  // Ordenar por nombre ascendente
        )

        while (cursor.moveToNext()) {
            clientes.add(cursorToCliente(cursor))
        }
        cursor.close()

        return clientes
    }
    fun getClienteById(id: Int): Cliente? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            "Clientes",
            null,
            "id_cliente = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )

        return if (cursor.moveToFirst()) {
            cursorToCliente(cursor).also { cursor.close() }
        } else {
            cursor.close()
            null
        }
    }


fun actualizarCliente(cliente: Cliente): Boolean {
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

    val rowsAffected = db.update(
        "Clientes",
        values,
        "id_cliente = ?",
        arrayOf(cliente.id_cliente.toString())
    )

    return rowsAffected > 0
}
    fun searchClientesByApellido(apellido: String): List<Cliente> {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            "Clientes",
            null,
            "apellido LIKE ?",
            arrayOf("$apellido%"), // Changed to start with the input
            null,
            null,
            "apellido ASC"
        )

        val clientes = mutableListOf<Cliente>()
        while (cursor.moveToNext()) {
            clientes.add(cursorToCliente(cursor))
        }
        cursor.close()

        return clientes
    }

private fun cursorToCliente(cursor: Cursor): Cliente {
    return Cliente(
        id_cliente = cursor.getIntOrNull(cursor.getColumnIndexOrThrow("id_cliente")),
        nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
        apellido = cursor.getString(cursor.getColumnIndexOrThrow("apellido")),
        documento = cursor.getString(cursor.getColumnIndexOrThrow("documento")),
        tipo_documento = cursor.getString(cursor.getColumnIndexOrThrow("tipo_documento")),
        telefono = cursor.getStringOrNull(cursor.getColumnIndexOrThrow("telefono")),
        email = cursor.getStringOrNull(cursor.getColumnIndexOrThrow("email")),
        direccion = cursor.getStringOrNull(cursor.getColumnIndexOrThrow("direccion"))
    )
}


// Aquí puedes agregar más métodos según sea necesario, como eliminarCliente, getAllClientes, etc.
}

