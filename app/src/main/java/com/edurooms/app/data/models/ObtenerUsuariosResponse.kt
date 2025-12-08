package com.edurooms.app.data.models

data class ObtenerUsuariosResponse(
    val mensaje: String,
    val usuarios: List<UsuarioData>
)
