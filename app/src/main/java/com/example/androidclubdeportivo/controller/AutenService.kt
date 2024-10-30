package com.example.androidclubdeportivo.controller

import android.content.Context
import android.content.SharedPreferences
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.example.androidclubdeportivo.model.dao.ClubDatabaseHelper
import java.security.MessageDigest

class AutenService(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("ClubDeportivoPrefs", Context.MODE_PRIVATE)

    private val dbHelper = ClubDatabaseHelper(context)

    companion object {
        private const val TAG = "AutenService"
    }

    fun login(email: String, password: String): Pair<String?, Int?> {
        val hashedPassword = hashPassword(password)
        var db: SQLiteDatabase? = null
        var cursor: Cursor? = null

        try {
            db = dbHelper.readableDatabase
            val query = """
                SELECT u.tipo_usuario, u.password, c.id_cliente, s.id_socio
                FROM Usuarios u
                LEFT JOIN Clientes c ON u.id_cliente = c.id_cliente
                LEFT JOIN Socios s ON c.id_cliente = s.id_cliente
                WHERE u.email = ?
            """
            cursor = db.rawQuery(query, arrayOf(email))

            if (cursor.moveToFirst()) {
                val passwordIndex = cursor.getColumnIndex("password")
                val userTypeIndex = cursor.getColumnIndex("tipo_usuario")
                val clienteIdIndex = cursor.getColumnIndex("id_cliente")
                val socioIdIndex = cursor.getColumnIndex("id_socio")

                if (passwordIndex >= 0 && userTypeIndex >= 0) {
                    val storedPassword = cursor.getString(passwordIndex)
                    val userType = cursor.getString(userTypeIndex)
                    val clienteId = if (clienteIdIndex >= 0) cursor.getInt(clienteIdIndex) else null
                    val socioId = if (socioIdIndex >= 0 && !cursor.isNull(socioIdIndex)) cursor.getInt(socioIdIndex) else null

                    if (hashedPassword == storedPassword) {
                        when {
                            userType == "Admin" -> {
                                Log.d(TAG, "Inicio de sesión exitoso para admin: $email")
                                return Pair("Admin", null)
                            }
                            userType == "Cliente" && socioId != null -> {
                                Log.d(TAG, "Inicio de sesión exitoso para socio: $email")
                                return Pair("Socio", clienteId)
                            }
                            else -> {
                                Log.d(TAG, "Usuario no tiene permisos para iniciar sesión: $email")
                            }
                        }
                    } else {
                        Log.d(TAG, "Contraseña incorrecta para $email")
                    }
                } else {
                    Log.e(TAG, "Error: No se encontraron las columnas necesarias en la tabla Usuarios.")
                }
            } else {
                Log.d(TAG, "Usuario no encontrado: $email")
            }
            return Pair(null, null)
        } catch (e: Exception) {
            Log.e(TAG, "Error durante el inicio de sesión: ${e.message}")
            return Pair(null, null)
        } finally {
            cursor?.close()
            db?.close()
        }
    }

    private fun hashPassword(password: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val hashBytes = md.digest(password.toByteArray())
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    fun rememberUser(email: String) {
        sharedPreferences.edit().putString("rememberedUser", email).apply()
    }

    fun getRememberedUser(): String? {
        return sharedPreferences.getString("rememberedUser", null)
    }

    fun clearRememberedUser() {
        sharedPreferences.edit().remove("rememberedUser").apply()
    }

    fun isUserRegistered(email: String): Boolean {
        var db: SQLiteDatabase? = null
        var cursor: Cursor? = null

        try {
            db = dbHelper.readableDatabase
            val query = "SELECT * FROM Usuarios WHERE email = ?"
            cursor = db.rawQuery(query, arrayOf(email))
            return cursor.count > 0
        } catch (e: Exception) {
            Log.e(TAG, "Error al comprobar si el usuario está registrado: ${e.message}")
            return false
        } finally {
            cursor?.close()
            db?.close()
        }
    }

    fun registerUser(email: String, password: String, tipoUsuario: String): Boolean {
        if (isUserRegistered(email)) {
            Log.e(TAG, "El usuario ya está registrado.")
            return false
        }

        val hashedPassword = hashPassword(password)
        var db: SQLiteDatabase? = null

        try {
            db = dbHelper.writableDatabase
            val query = "INSERT INTO Usuarios (email, password, tipo_usuario) VALUES (?, ?, ?)"
            db.execSQL(query, arrayOf(email, hashedPassword, tipoUsuario))
            Log.d(TAG, "Usuario registrado exitosamente: $email")
            return true
        } catch (e: Exception) {
            Log.e(TAG, "Error al registrar usuario: ${e.message}")
            return false
        } finally {
            db?.close()
        }
    }
}