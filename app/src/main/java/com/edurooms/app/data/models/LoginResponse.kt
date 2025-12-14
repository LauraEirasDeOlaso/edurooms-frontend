package com.edurooms.app.data.models

data class LoginResponse(
    val mensaje: String,
    val usuario: UsuarioData,
    val token: String
)

data class UsuarioData(
    val id: Int,
    val nombre: String,
    val email: String,
    val rol: String,
    val estado: String = "habilitado",
    val departamento: String = "",
    val primera_vez_login: Boolean = false
)

data class PerfílResponse(
    val usuario: UsuarioData
)


