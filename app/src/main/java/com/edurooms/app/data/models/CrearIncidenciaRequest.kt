package com.edurooms.app.data.models

data class CrearIncidenciaRequest(
    val aula_id: Int,
    val descripcion: String,
    val tipo: String
)