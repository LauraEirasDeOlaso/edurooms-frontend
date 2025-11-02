package com.edurooms.app.data.utils

import android.content.Context
import android.content.SharedPreferences

class TokenManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        "edurooms_prefs",
        Context.MODE_PRIVATE
    )

    fun guardarToken(token: String) {
        prefs.edit().putString("auth_token", token).apply()
    }

    fun obtenerToken(): String? {
        return prefs.getString("auth_token", null)
    }

    fun eliminarToken() {
        prefs.edit().remove("auth_token").apply()
    }

    fun tokenValido(): Boolean {
        return !obtenerToken().isNullOrEmpty()
    }
}