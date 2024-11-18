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


        insertInitialSedes(db)
        insertInitialProfesores(db)
        insertInitialActividades(db)
        insertInitialHorarios(db)

        Log.d("ClubDatabaseHelper", "Base de datos creada exitosamente con datos iniciales")
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

    private fun insertInitialSedes(db: SQLiteDatabase) {
        val sedes = listOf(
            "Sucursal Cerro",
            "Sucursal Córdoba"
        )

        for (nombre in sedes) {
            val values = ContentValues().apply {
                put("nombre", nombre)
                put("direccion", "Dirección de $nombre")
            }
            db.insert("Sedes", null, values)
        }
    }


    private fun insertInitialProfesores(db: SQLiteDatabase) {
        val profesores = listOf(
            Triple(1, "Juan", "Donadio"),
            Triple(2, "María", "Nieves"),
            Triple(3, "Carlos", "Gardel")
        )

        for ((id, nombre, apellido) in profesores) {
            val values = ContentValues().apply {
                put("id_profesor", id)
                put("nombre", nombre)
                put("apellido", apellido)
                put("documento", "${id}2345678")
                put("telefono", "${id}23456789")
                put("email", "${nombre.toLowerCase()}@example.com")
            }
            db.insert("Profesores", null, values)
        }
    }

    private fun insertInitialActividades(db: SQLiteDatabase) {
        val actividades = listOf(
            arrayOf(1, "Tenis", 1, 3),
            arrayOf(2, "Yoga", 3, 4),
            arrayOf(3, "Pilates", 1, 4),
            arrayOf(4, "Spinning", 1, 3),
            arrayOf(5, "Zumba", 1, 3),
            arrayOf(6, "Boxeo", 1, 3),
            arrayOf(7, "Basquet", 1, 3),
            arrayOf(8, "Yoga", 1, 4),
            arrayOf(9, "Yoga", 1, 4),
            arrayOf(10, "Natación", 2, 4)
        )

        for ((id, nombre, idProfesor, idSede) in actividades) {
            val values = ContentValues().apply {
                put("id_actividad", id as Int)
                put("nombre", nombre as String)
                put("cupo", 20)
                put("precio", 1000.0)
                put("id_profesor", idProfesor as Int)
                put("id_sede", idSede as Int)
            }
            db.insert("Actividades", null, values)
        }
    }

    private fun insertInitialHorarios(db: SQLiteDatabase) {
        val horarios = listOf(
            arrayOf(1, 1, "Lunes", "10:00", "11:00"),
            arrayOf(2, 1, "Miércoles", "10:00", "11:00"),
            arrayOf(3, 2, "Lunes", "10:00", "11:00"),
            arrayOf(4, 2, "Miércoles", "10:00", "11:00"),
            arrayOf(5, 3, "Lunes", "10:00", "11:00"),
            arrayOf(6, 3, "Miércoles", "10:00", "11:00"),
            arrayOf(7, 4, "Lunes", "10:00", "11:00"),
            arrayOf(8, 4, "Miércoles", "10:00", "11:00"),
            arrayOf(9, 5, "Lunes", "10:00", "11:00"),
            arrayOf(10, 5, "Miércoles", "10:00", "11:00"),
            arrayOf(11, 6, "Lunes", "10:00", "11:00"),
            arrayOf(12, 6, "Miércoles", "10:00", "11:00"),
            arrayOf(13, 7, "Lunes", "10:00", "11:00"),
            arrayOf(14, 7, "Miércoles", "10:00", "11:00"),
            arrayOf(15, 1, "Lunes", "10:00", "11:00"),
            arrayOf(16, 1, "Miércoles", "10:00", "11:00"),
            arrayOf(17, 2, "Martes", "10:00", "11:00"),
            arrayOf(18, 3, "Jueves", "10:00", "11:00"),
            arrayOf(19, 4, "Lunes", "11:00", "12:00"),
            arrayOf(20, 5, "Miércoles", "11:00", "12:00"),
            arrayOf(21, 6, "Viernes", "11:00", "12:00"),
            arrayOf(22, 10, "Martes", "09:00", "10:00")
        )

        for ((id, idActividad, diaSemana, horaInicio, horaFin) in horarios) {
            val values = ContentValues().apply {
                put("id_horario", id as Int)
                put("id_actividad", idActividad as Int)
                put("dia_semana", diaSemana as String)
                put("hora_inicio", horaInicio as String)
                put("hora_fin", horaFin as String)
            }
            db.insert("HorariosActividad", null, values)
        }
    }

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
            // Obtenemos los índices de las columnas
            val idIndex = cursor.getColumnIndex("id_usuario")
            val emailIndex = cursor.getColumnIndex("email")
            val passwordIndex = cursor.getColumnIndex("password")
            val tipoUsuarioIndex = cursor.getColumnIndex("tipo_usuario")

            // Verificamos que los índices son válidos (diferentes de -1)
            if (idIndex != -1 && emailIndex != -1 && passwordIndex != -1 && tipoUsuarioIndex != -1) {
                val id = cursor.getLong(idIndex)
                val email = cursor.getString(emailIndex)
                val password = cursor.getString(passwordIndex)
                val tipoUsuario = cursor.getString(tipoUsuarioIndex)
                println("ID: $id, Email: $email, Password: $password, Tipo: $tipoUsuario")
            } else {
                println("Error: una o más columnas no se encontraron en la tabla Usuarios.")
            }
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
}