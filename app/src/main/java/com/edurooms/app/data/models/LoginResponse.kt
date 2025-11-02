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
    val rol: String
)


