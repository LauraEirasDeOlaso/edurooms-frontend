package com.edurooms.app.data.utils

object Constants {

    // Base URL del backend
    const val BASE_URL = "http://10.0.2.2:3000/api/"  // Para emulador
    // Dispositivo físico: "http://TU_IP_LOCAL:3000/api/"

    // Endpoints
    const val ENDPOINT_LOGIN = "auth/login"
    const val ENDPOINT_REGISTRO = "auth/registro"
    const val ENDPOINT_PERFIL = "auth/perfil"
    const val ENDPOINT_AULAS = "aulas"
    const val ENDPOINT_AULA_DETALLE = "aulas/{id}"
    const val ENDPOINT_RESERVAS = "reservas"
    const val ENDPOINT_INCIDENCIAS = "incidencias"

    // SharedPreferences
    const val PREFS_TOKEN_KEY = "auth_token"
    const val PREFS_USER_KEY = "user_data"
    const val PREFS_NAME = "edurooms_prefs"
}