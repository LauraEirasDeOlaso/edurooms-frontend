package com.edurooms.app.data.models

data class DisponibilidadResponse(
    val mensaje: String,
    val disponibilidad: Disponibilidad
)

data class Disponibilidad(
    val aula_id: Int,
    val fecha: String,
    val horariosLibres: List<Horario>,
    val horariosOcupados: List<Horario>,
    val totalLibres: Int,
    val totalOcupados: Int
)

data class Horario(
    val hora_inicio: String,
    val hora_fin: String
)
