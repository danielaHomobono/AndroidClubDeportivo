package com.example.androidclubdeportivo.model

import java.io.Serializable


data class Cliente(
        val id_cliente: Int? = null,
        val nombre: String,
        val apellido: String,
        val documento: String,
        val tipo_documento: String,
        val telefono: String?,
        val email: String?,
        val direccion: String?,
        val fechaUltimoPago: String?

    ): Serializable

