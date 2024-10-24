package com.example.androidclubdeportivo.model.dao

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.security.MessageDigest

class ClubDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "ClubDeportivo.db"
        private const val DATABASE_VERSION = 3
    }

    override fun onCreate(db: SQLiteDatabase) {
        Log.d("ClubDatabaseHelper", "Creando base de datos")

        // Tabla de Clientes
        db.execSQL(
            """
            CREATE TABLE Clientes (
                id_cliente INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                apellido TEXT NOT NULL,
                documento TEXT UNIQUE NOT NULL,
                tipo_documento TEXT NOT NULL,
                telefono TEXT,
                email TEXT UNIQUE,
                direccion TEXT
            )
        """
        )

        // Tabla de Socios
        db.execSQL(
            """
            CREATE TABLE Socios (
                id_socio INTEGER PRIMARY KEY AUTOINCREMENT,
                id_cliente INTEGER NOT NULL,
                cuota_fija REAL NOT NULL,
                FOREIGN KEY (id_cliente) REFERENCES Clientes(id_cliente) ON DELETE CASCADE
            )
        """
        )

        // Tabla de Usuarios
        db.execSQL(
            """
            CREATE TABLE Usuarios (
                id_usuario INTEGER PRIMARY KEY AUTOINCREMENT,        
                email TEXT UNIQUE NOT NULL,
                password TEXT NOT NULL,
                tipo_usuario TEXT CHECK (tipo_usuario IN ('Admin', 'Cliente')) NOT NULL,
                id_cliente INTEGER,
                FOREIGN KEY (id_cliente) REFERENCES Clientes(id_cliente) ON DELETE SET NULL
            )
        """
        )

        // Tabla de Sedes
        db.execSQL(
            """
            CREATE TABLE Sedes (
                id_sede INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                direccion TEXT NOT NULL
            )
        """
        )

        // Tabla de Profesores
        db.execSQL(
            """
            CREATE TABLE Profesores (
                id_profesor INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                apellido TEXT NOT NULL,
                documento TEXT UNIQUE NOT NULL,
                telefono TEXT,
                email TEXT
            )
        """
        )

        // Tabla de Actividades
        db.execSQL(
            """
            CREATE TABLE Actividades (
                id_actividad INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                cupo INTEGER DEFAULT 0,
                precio REAL DEFAULT 0.0,
                id_profesor INTEGER NOT NULL,
                id_sede INTEGER NOT NULL,
                FOREIGN KEY (id_profesor) REFERENCES Profesores(id_profesor),
                FOREIGN KEY (id_sede) REFERENCES Sedes(id_sede)
            )
        """
        )

        // Tabla de Horarios de Actividades
        db.execSQL(
            """
            CREATE TABLE HorariosActividad (
                id_horario INTEGER PRIMARY KEY AUTOINCREMENT,
                id_actividad INTEGER NOT NULL,
                dia_semana TEXT NOT NULL,
                hora_inicio TEXT NOT NULL,
                hora_fin TEXT NOT NULL,
                FOREIGN KEY (id_actividad) REFERENCES Actividades(id_actividad) ON DELETE CASCADE
            )
        """
        )

        // Tabla de Inscripciones
        db.execSQL(
            """
            CREATE TABLE Inscripciones (
                id_inscripcion INTEGER PRIMARY KEY AUTOINCREMENT,
                id_cliente INTEGER NOT NULL,
                id_actividad INTEGER NOT NULL,
                id_horario INTEGER NOT NULL,
                fecha DATE NOT NULL,
                FOREIGN KEY (id_cliente) REFERENCES Clientes(id_cliente),
                FOREIGN KEY (id_actividad) REFERENCES Actividades(id_actividad),
                FOREIGN KEY (id_horario) REFERENCES HorariosActividad(id_horario)
            )
        """
        )

        // Tabla de Cuotas
        db.execSQL(
            """
            CREATE TABLE Cuotas (
                id_cuota INTEGER PRIMARY KEY AUTOINCREMENT,
                id_cliente INTEGER NOT NULL,
                monto REAL NOT NULL,
                fecha_vencimiento DATE NOT NULL,
                estado TEXT CHECK (estado IN ('Vencido', 'Al día')) NOT NULL,
                fecha_pago DATE,
                FOREIGN KEY (id_cliente) REFERENCES Clientes(id_cliente) ON DELETE CASCADE
            )
        """
        )

        // Tabla de Pagos
        db.execSQL(
            """
            CREATE TABLE Pagos (
                id_pago INTEGER PRIMARY KEY AUTOINCREMENT,
                id_inscripcion INTEGER,
                id_cuota INTEGER NOT NULL,
                fecha_pago DATE NOT NULL,
                monto_pago REAL NOT NULL,
                FOREIGN KEY (id_inscripcion) REFERENCES Inscripciones(id_inscripcion),
                FOREIGN KEY (id_cuota) REFERENCES Cuotas(id_cuota)
            )
        """
        )

        Log.d("ClubDatabaseHelper", "Base de datos creada exitosamente")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 3) {
            // Crear la nueva tabla HorariosActividad si no existe
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS HorariosActividad (
                    id_horario INTEGER PRIMARY KEY AUTOINCREMENT,
                    id_actividad INTEGER NOT NULL,
                    dia_semana TEXT NOT NULL,
                    hora_inicio TEXT NOT NULL,
                    hora_fin TEXT NOT NULL,
                    FOREIGN KEY (id_actividad) REFERENCES Actividades(id_actividad) ON DELETE CASCADE
                )
            """
            )

            // Modificar la tabla Inscripciones para incluir id_horario
            db.execSQL("ALTER TABLE Inscripciones ADD COLUMN id_horario INTEGER")
        }
    }

    // Métodos auxiliares

    fun insertTestUser(email: String, password: String, tipoUsuario: String): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("email", email)
            put("password", password)
            put("tipo_usuario", tipoUsuario)
        }
        return db.insert("Usuarios", null, values)
    }

    fun printUsersTable() {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM Usuarios", null)
        println("Contenido de la tabla Usuarios:")
        while (cursor.moveToNext()) {
            val id = cursor.getLong(cursor.getColumnIndex("id_usuario"))
            val email = cursor.getString(cursor.getColumnIndex("email"))
            val password = cursor.getString(cursor.getColumnIndex("password"))
            val tipoUsuario = cursor.getString(cursor.getColumnIndex("tipo_usuario"))
            println("ID: $id, Email: $email, Password: $password, Tipo: $tipoUsuario")
        }
        cursor.close()
    }

    fun insertUsuario(
        email: String,
        password: String,
        tipoUsuario: String,
        idCliente: Long?
    ): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("email", email)
            put("password", hashPassword(password))
            put("tipo_usuario", tipoUsuario)
            put("id_cliente", idCliente)
        }
        return db.insert("Usuarios", null, values)
    }

    fun isEmailRegistered(email: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM Usuarios WHERE email = ?", arrayOf(email))
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    private fun hashPassword(password: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(password.toByteArray())
        return digest.fold("", { str, it -> str + "%02x".format(it) })
    }
    fun limpiarTablaSedes() {
        val db = this.writableDatabase
        db.delete("Sedes", null, null)
        Log.d("ClubDatabaseHelper", "Tabla Sedes limpiada")
    }
    fun insertTestSedes() {
        val db = this.writableDatabase
        val sedes = listOf("Sucursal Cerro", "Sucursal Córdoba")

        // Primero, limpia la tabla
        limpiarTablaSedes()

        for (sede in sedes) {
            val values = ContentValues().apply {
                put("nombre", sede)
                put("direccion", "Dirección de $sede")
            }
            db.insert("Sedes", null, values)
            Log.d("ClubDatabaseHelper", "Sede insertada: $sede")
        }
    }

    fun getAllSedes(): List<String> {
        val sedesDeseadas = listOf("Sucursal Cerro", "Sucursal Córdoba")
        val sedes = mutableListOf<String>()
        val db = this.readableDatabase
        val cursor = db.query(
            "Sedes",
            arrayOf("nombre"),
            "nombre IN (?, ?)",
            sedesDeseadas.toTypedArray(),
            null,
            null,
            "nombre ASC"
        )

        while (cursor.moveToNext()) {
            sedes.add(cursor.getString(0))
        }
        cursor.close()
        Log.d("ClubDatabaseHelper", "Sedes obtenidas: $sedes")
        return sedes
    }

    fun insertTestActividades() {
        val db = this.writableDatabase
        val actividades = listOf(
            Triple("Yoga", 1, 1),
            Triple("Pilates", 2, 2),
            Triple("Spinning", 3, 3)
        )
        for ((nombre, idProfesor, idSede) in actividades) {
            val values = ContentValues().apply {
                put("nombre", nombre)
                put("cupo", 20)
                put("precio", 1000.0)
                put("id_profesor", idProfesor)
                put("id_sede", idSede)
            }
            db.insert("Actividades", null, values)
        }
        Log.d("ClubDatabaseHelper", "Actividades de prueba insertadas")
    }
    fun printAllTables() {
        val tables = listOf("Sedes", "Actividades", "HorariosActividad", "Clientes", "Inscripciones")
        val db = readableDatabase
        for (table in tables) {
            val cursor = db.rawQuery("SELECT * FROM $table", null)
            Log.d("ClubDatabaseHelper", "Contenido de la tabla $table:")
            while (cursor.moveToNext()) {
                val rowData = (0 until cursor.columnCount).map {
                    "${cursor.getColumnName(it)}: ${cursor.getString(it)}"
                }.joinToString(", ")
                Log.d("ClubDatabaseHelper", rowData)
            }
            cursor.close()
        }
    }

}
