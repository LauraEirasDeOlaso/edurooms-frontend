package com.edurooms.app.data.models


data class CambiarPasswordResponse(
    val mensaje: String,
    val usuario: CambiarPasswordUsuario
)

data class CambiarPasswordUsuario(
    val id: Int,
    val email: String,
    val primera_vez_login: Boolean
)

data class CambiarPasswordRequest(
    val passwordActual: String,
    val passwordNueva: String,
    val passwordNuevaConfirmar: String,
    val esPrimeraVez: Boolean
)
