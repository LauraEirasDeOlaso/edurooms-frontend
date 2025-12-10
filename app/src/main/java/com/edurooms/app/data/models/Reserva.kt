package com.edurooms.app.data.models

data class Reserva(
    val id: Int,
    val usuario_id: Int,
    val aula_id: Int,
    val fecha: String,
    val hora_inicio: String,
    val hora_fin: String,
    val estado: String,
    val aula_nombre: String,
    val usuario_nombre: String? = null
)
