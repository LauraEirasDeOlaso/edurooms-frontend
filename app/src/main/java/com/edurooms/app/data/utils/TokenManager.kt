package com.edurooms.app.data.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class TokenManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        "edurooms_prefs",
        Context.MODE_PRIVATE
    )

    fun guardarToken(token: String) {
        prefs.edit { putString("auth_token", token) }
    }

    fun obtenerToken(): String? {
        return prefs.getString("auth_token", null)
    }

    fun eliminarToken() {
        prefs.edit { remove("auth_token") }
    }

    fun tokenValido(): Boolean {
        return !obtenerToken().isNullOrEmpty()
    }

    fun guardarRol(rol: String) {
        prefs.edit { putString("user_rol", rol) }
    }

    fun obtenerRol(): String {
        return prefs.getString("user_rol", "profesor") ?: "profesor"
    }
}