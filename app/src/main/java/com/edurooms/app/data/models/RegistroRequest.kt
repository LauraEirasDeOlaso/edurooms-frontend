package com.edurooms.app.data.models

data class RegistroRequest(
    val nombre: String,
    val email: String,
    val password: String,
    val confirmarPassword: String,
    val rol: String = "profesor"
)
