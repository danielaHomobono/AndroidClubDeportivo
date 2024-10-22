package com.example.androidclubdeportivo.model




    data class Cliente(
        val id_cliente: Int? = null,
        val nombre: String,
        val apellido: String,
        val documento: String,
        val tipo_documento: String,
        val telefono: String?,
        val email: String?,
        val direccion: String?
    )

