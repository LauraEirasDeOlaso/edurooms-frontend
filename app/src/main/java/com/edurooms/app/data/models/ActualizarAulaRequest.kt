package com.edurooms.app.data.models

data class ActualizarAulaRequest(
    val nombre: String,
    val capacidad: Int,
    val ubicacion: String?,
    val estado: String
)

