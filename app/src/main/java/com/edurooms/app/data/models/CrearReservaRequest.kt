package com.edurooms.app.data.models

data class CrearReservaRequest(
    val usuario_id: Int,
    val aula_id: Int,
    val fecha: String,
    val hora_inicio: String,
    val hora_fin: String
)
