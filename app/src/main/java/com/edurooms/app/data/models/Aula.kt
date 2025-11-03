package com.edurooms.app.data.models

data class Aula(
    val id: Int,
    val nombre: String,
    val capacidad: Int,
    val ubicacion: String,
    val codigo_qr: String,
    val estado: String,
    val created_at: String,
    val update_at: String
)
