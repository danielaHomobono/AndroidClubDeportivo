package com.example.androidclubdeportivo.model.dao

import android.content.ContentValues
import android.database.Cursor
import android.util.Log
import androidx.core.database.getIntOrNull
import androidx.core.database.getStringOrNull
import com.example.androidclubdeportivo.model.Cliente
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class ClienteDAO(private val dbHelper: ClubDatabaseHelper) {


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

    fun inscribirSocio(id_cliente: Long, cuotaFija: Double): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("id_cliente", id_cliente)
            put("cuota_fija", cuotaFija)
        }
        val socioId = db.insert("Socios", null, values)

        if (socioId != -1L) {
            val fechaHoy = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val fechaVencimiento = calcularFechaVencimiento(fechaHoy)
            insertarCuota(id_cliente, cuotaFija, fechaVencimiento, "Vencido") // Cambiado a "Vencido"
        }

        return socioId
    }
    fun calcularFechaVencimiento(fechaInicio: String): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val fechaActual = Date()
        return sdf.format(fechaActual)
    }




    fun insertarCuota(idCliente: Long, monto: Double, fechaVencimiento: String, estado: String): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("id_cliente", idCliente)
            put("monto", monto)
            put("fecha_vencimiento", fechaVencimiento)
            put("estado", estado)
        }
        return db.insert("Cuotas", null, values)
    }



    fun isEmailRegistered(email: String): Boolean {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM Clientes WHERE email = ?", arrayOf(email))
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    fun searchClientes(query: String): List<Cliente> {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            """
        SELECT * FROM Clientes
        WHERE id_cliente IN (
            SELECT id_cliente FROM ClientesFTS
            WHERE ClientesFTS MATCH ?
        )
    """, arrayOf(query)
        )

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
            "nombre ASC"
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

    fun getClienteByDocumento(tipoDocumento: String, numeroDocumento: String): Int? {
        val db = dbHelper.readableDatabase
        var clienteId: Int? = null
        val cursor = db.query(
            "Clientes",
            arrayOf("id_cliente"),
            "tipo_documento = ? AND documento = ?",
            arrayOf(tipoDocumento, numeroDocumento),
            null,
            null,
            null
        )

        if (cursor.moveToFirst()) {
            clienteId = cursor.getInt(0)
            Log.d("ClienteDAO", "Cliente encontrado con ID: $clienteId")
        } else {
            Log.d(
                "ClienteDAO",
                "No se encontró cliente con documento: $tipoDocumento - $numeroDocumento"
            )
        }
        cursor.close()
        return clienteId
    }

    fun obtenerClientePorDocumento(tipoDocumento: String, numeroDocumento: String): Cliente? {
        val clienteId = getClienteByDocumento(tipoDocumento, numeroDocumento)
        return clienteId?.let { getClienteById(it) } // Llama a getClienteById para obtener  Cliente completo
    }

    fun searchClientesByApellido(apellido: String): List<Cliente> {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            "Clientes",
            null,
            "apellido LIKE ?",
            arrayOf("$apellido%"),
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




    fun getClientesAlDia(): List<Cliente> {
        val clientes = mutableListOf<Cliente>()
        val db = dbHelper.readableDatabase
        var cursor: Cursor? = null

        try {
            cursor = db.rawQuery(
                """
            SELECT c.id_cliente, c.nombre, c.apellido, c.documento, c.tipo_documento, c.telefono, c.email, c.direccion, cu.fecha_vencimiento AS fechaUltimoPago
            FROM Clientes c
            JOIN Cuotas cu ON c.id_cliente = cu.id_cliente
            WHERE cu.estado = 'Al día'
            """, null
            )

            Log.d("ColumnNames", "Columnas disponibles: ${cursor.columnNames.joinToString()}")

            if (cursor.moveToFirst()) {
                do {
                    val cliente = Cliente(
                        id_cliente = cursor.getInt(cursor.getColumnIndexOrThrow("id_cliente")),
                        nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                        apellido = cursor.getString(cursor.getColumnIndexOrThrow("apellido")),
                        documento = cursor.getString(cursor.getColumnIndexOrThrow("documento")),
                        tipo_documento = cursor.getString(cursor.getColumnIndexOrThrow("tipo_documento")),
                        telefono = cursor.getStringOrNull(cursor.getColumnIndex("telefono")),
                        email = cursor.getStringOrNull(cursor.getColumnIndex("email")),
                        direccion = cursor.getStringOrNull(cursor.getColumnIndex("direccion")),
                        //fechaUltimoPago = cursor.getStringOrNull(cursor.getColumnIndex("fechaUltimoPago"))
                    )
                    clientes.add(cliente)
                } while (cursor.moveToNext())
            }
        } catch (e: Exception) {
            Log.e("ClienteDAO", "Error al obtener clientes al día: ${e.message}")
        } finally {
            cursor?.close()
        }

        return clientes
    }


    fun getClientesConCuotasVencidas(): List<Cliente> {
        val clientes = mutableListOf<Cliente>()
        val db = dbHelper.readableDatabase
        var cursor: Cursor? = null

        try {
            cursor = db.rawQuery(
                """
            SELECT c.id_cliente, c.nombre, c.apellido, c.documento, c.tipo_documento, c.telefono, c.email, c.direccion
            FROM Clientes c
            JOIN Cuotas cu ON c.id_cliente = cu.id_cliente
            WHERE cu.estado = 'Vencido'
            """, null
            )

            Log.d("ColumnNames", "Columnas disponibles: ${cursor.columnNames.joinToString()}")

            if (cursor.moveToFirst()) {
                do {
                    val fechaUltimoPagoColumnIndex = cursor.getColumnIndex("fechaUltimoPago")
                    val fechaUltimoPago = if (fechaUltimoPagoColumnIndex != -1) {
                        cursor.getStringOrNull(fechaUltimoPagoColumnIndex)
                    } else {
                        null
                    }

                    val cliente = Cliente(
                        id_cliente = cursor.getInt(cursor.getColumnIndexOrThrow("id_cliente")),
                        nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                        apellido = cursor.getString(cursor.getColumnIndexOrThrow("apellido")),
                        documento = cursor.getString(cursor.getColumnIndexOrThrow("documento")),
                        tipo_documento = cursor.getString(cursor.getColumnIndexOrThrow("tipo_documento")),
                        telefono = cursor.getStringOrNull(cursor.getColumnIndex("telefono")),
                        email = cursor.getStringOrNull(cursor.getColumnIndex("email")),
                        direccion = cursor.getStringOrNull(cursor.getColumnIndex("direccion")),
                        //fechaUltimoPago = fechaUltimoPago // Asignar el valor verificado
                    )
                    clientes.add(cliente)
                } while (cursor.moveToNext())
            }
        } catch (e: Exception) {
            Log.e("ClienteDAO", "Error al obtener clientes vencidos: ${e.message}")
        } finally {
            cursor?.close()
        }

        return clientes
    }
    fun getClientesConCuotasVencenHoy(): List<Cliente> {
        val clientes = mutableListOf<Cliente>()
        val db = dbHelper.readableDatabase
        val fechaHoy = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        var cursor: Cursor? = null

        try {
            cursor = db.rawQuery(
                """
            SELECT DISTINCT c.id_cliente, c.nombre, c.apellido, c.documento, c.tipo_documento, c.telefono, c.email, c.direccion
            FROM Clientes c
            JOIN Cuotas cu ON c.id_cliente = cu.id_cliente
            WHERE cu.fecha_vencimiento = ?
            """, arrayOf(fechaHoy)
            )

            Log.d("ColumnNames", "Columnas disponibles: ${cursor.columnNames.joinToString()}")

            if (cursor.moveToFirst()) {
                do {
                    val cliente = cursorToCliente(cursor)
                    clientes.add(cliente)
                } while (cursor.moveToNext())
            }
        } catch (e: Exception) {
            Log.e("ClienteDAO", "Error al obtener clientes con cuotas que vencen hoy: ${e.message}")
        } finally {
            cursor?.close()
        }

        return clientes
    }
    private fun cursorToCliente(cursor: Cursor): Cliente {
        return Cliente(
            id_cliente = cursor.getIntOrNull(cursor.getColumnIndexOrThrow("id_cliente")),
            nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
            apellido = cursor.getString(cursor.getColumnIndexOrThrow("apellido")),
            documento = cursor.getString(cursor.getColumnIndexOrThrow("documento")),
            tipo_documento = cursor.getString(cursor.getColumnIndexOrThrow("tipo_documento")),
            telefono = cursor.getStringOrNull(cursor.getColumnIndex("telefono")),
            email = cursor.getStringOrNull(cursor.getColumnIndex("email")),
            direccion = cursor.getStringOrNull(cursor.getColumnIndex("direccion")),
            //fechaUltimoPago = cursor.getStringOrNull(cursor.getColumnIndex("fecha-Pago"))
        )
    }
}
