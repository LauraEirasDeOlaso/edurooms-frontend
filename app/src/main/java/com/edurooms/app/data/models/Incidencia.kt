package com.edurooms.app.data.models

data class Incidencia(
    val id: Int,
    val aula_id: Int,
    val usuario_id: Int,
    val descripcion: String,
    val tipo: String,
    val estado: String,
    val aula_nombre: String,
    val usuario_nombre: String
)
