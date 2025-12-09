package com.edurooms.app.data.models

data class CrearUsuarioResponse(
    val mensaje: String,
    val usuario: CrearUsuarioRequest,
    val passwordTemporal: String
)
