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
        private const val DATABASE_VERSION = 2
    }

    override fun onCreate(db: SQLiteDatabase) {
        Log.d("ClubDatabaseHelper", "Creando base de datos")
        // ... (resto del código)
        Log.d("ClubDatabaseHelper", "Base de datos creada exitosamente")
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

        // Tabla de Usuarios nota: no se si incluir: nombre TEXT NOT NULL,
        db.execSQL(
            """
    CREATE TABLE Usuarios (
        id_usuario INTEGER PRIMARY KEY AUTOINCREMENT,        
        email TEXT UNIQUE NOT NULL,  -- Email para el inicio de sesión
        password TEXT NOT NULL,  -- Debe almacenarse como un hash
        tipo_usuario TEXT CHECK (tipo_usuario IN ('Admin', 'Cliente')) NOT NULL,
        id_cliente INTEGER,  -- Relación con cliente si es un usuario tipo Cliente
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

        // Tabla de Sesiones
        db.execSQL(
            """
            CREATE TABLE Sesiones (
                id_sesion INTEGER PRIMARY KEY AUTOINCREMENT,
                id_actividad INTEGER NOT NULL,
                fecha DATE NOT NULL,
                horario TEXT NOT NULL,
                FOREIGN KEY (id_actividad) REFERENCES Actividades(id_actividad)
            )
        """
        )

        // Tabla de Inscripciones
        db.execSQL(
            """
            CREATE TABLE Inscripciones (
                id_inscripcion INTEGER PRIMARY KEY AUTOINCREMENT,
                id_socio INTEGER,
                id_cliente INTEGER,
                id_sesion INTEGER,
                fecha DATE,
                FOREIGN KEY (id_socio) REFERENCES Socios(id_socio),
                FOREIGN KEY (id_cliente) REFERENCES Clientes(id_cliente),
                FOREIGN KEY (id_sesion) REFERENCES Sesiones(id_sesion)
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


    }


    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            // Agrega la columna apellido si no existe
            db.execSQL("ALTER TABLE Clientes ADD COLUMN apellido TEXT")

            db.execSQL("DROP TABLE IF EXISTS Pagos")
            db.execSQL("DROP TABLE IF EXISTS Cuotas")
            db.execSQL("DROP TABLE IF EXISTS Inscripciones")
            db.execSQL("DROP TABLE IF EXISTS Sesiones")
            db.execSQL("DROP TABLE IF EXISTS Actividades")
            db.execSQL("DROP TABLE IF EXISTS Profesores")
            db.execSQL("DROP TABLE IF EXISTS Sedes")
            db.execSQL("DROP TABLE IF EXISTS Socios")
            db.execSQL("DROP TABLE IF EXISTS Usuarios")
            db.execSQL("DROP TABLE IF EXISTS Clientes")
            onCreate(db)
        }
    }
        fun insertTestUser(email: String, password: String, tipoUsuario: String): Long {
            val db = this.writableDatabase
            val values = ContentValues().apply {
                put("email", email)
                put("password", password)  // Almacena la contraseña sin hashear
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
            db.close()
        }

        fun insertUsuario(
            email: String,
            password: String,
            tipoUsuario: String,
            idCliente: Long?
        ): Long {
            val db = this.writableDatabase
            val values = ContentValues()

            values.put("email", email)
            values.put("password", hashPassword(password)) // Almacenamos la contraseña con hash
            values.put("tipo_usuario", tipoUsuario)
            values.put("id_cliente", idCliente)

            return db.insert("Usuarios", null, values)
        }

        fun isEmailRegistered(email: String): Boolean {
            val db = this.readableDatabase
            val cursor = db.rawQuery("SELECT * FROM Usuarios WHERE email = ?", arrayOf(email))
            val exists = cursor.count > 0
            cursor.close()
            return exists
        }

        // Función para generar el hash de la contraseña
        private fun hashPassword(password: String): String {
            val md = MessageDigest.getInstance("SHA-256")
            val digest = md.digest(password.toByteArray())
            return digest.fold("", { str, it -> str + "%02x".format(it) })
        }


    }




