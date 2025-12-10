package com.edurooms.app.data.models

data class CrearAulaRequest(
    val nombre: String,
    val capacidad: Int,
    val ubicacion: String?,
    val codigo_qr: String?
)
