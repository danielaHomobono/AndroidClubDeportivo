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

    fun login(email: String, password: String): Boolean {
        val hashedPassword = hashPassword(password)
        var db: SQLiteDatabase? = null
        var cursor: Cursor? = null

        try {
            db = dbHelper.readableDatabase
            val query = "SELECT * FROM Usuarios WHERE email = ?"
            cursor = db.rawQuery(query, arrayOf(email))

            if (cursor.moveToFirst()) {
                val storedPassword = cursor.getString(cursor.getColumnIndex("password"))
                val result = hashedPassword == storedPassword
                Log.d(TAG, "Login attempt for $email: ${if (result) "successful" else "failed"}")
                Log.d(TAG, "Stored password: $storedPassword")
                Log.d(TAG, "Hashed input password: $hashedPassword")
                return result
            }
            return false
        } catch (e: Exception) {
            Log.e(TAG, "Error durante el inicio de sesión: ${e.message}")
            return false
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