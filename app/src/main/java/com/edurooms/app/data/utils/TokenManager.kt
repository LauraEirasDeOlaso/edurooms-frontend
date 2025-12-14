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

    // Guardar nombre
    fun guardarNombre(nombre: String) {
        prefs.edit().putString("user_name", nombre).apply()
    }

    // Obtener nombre
    fun obtenerNombre(): String? {
        return prefs.getString("user_name", null)
    }

    // Guardar email
    fun guardarEmail(email: String) {
        prefs.edit().putString("user_email", email).apply()
    }

    // Obtener email
    fun obtenerEmail(): String? {
        return prefs.getString("user_email", null)
    }

    // Guardar departamento (opcional)
    fun guardarDepartamento(departamento: String) {
        prefs.edit().putString("user_department", departamento).apply()
    }

    // Obtener departamento (opcional)
    fun obtenerDepartamento(): String? {
        return prefs.getString("user_department", null)
    }

    fun guardarRol(rol: String) {
        prefs.edit { putString("user_rol", rol) }
    }

    fun obtenerRol(): String {
        return prefs.getString("user_rol", "profesor") ?: "profesor"
    }

    fun guardarPrimeraVezLogin(primeraVez: Boolean) {
        prefs.edit().putBoolean("primera_vez_login", primeraVez).apply()
    }

    fun obtenerPrimeraVezLogin(): Boolean {
        return prefs.getBoolean("primera_vez_login", false)
    }

    fun guardarIdUsuario(id: Int) {
        prefs.edit().putInt("usuario_id", id).apply()
    }

    fun obtenerIdUsuario(): Int {
        return prefs.getInt("usuario_id", 0)
    }

    // Recuérdame - Guardar email y contraseña
    fun guardarCredencialesRecordadas(email: String, password: String) {
        prefs.edit {
            putString("remember_email", email)
            putString("remember_password", password)
        }
    }

    // Obtener email recordado
    fun obtenerEmailRecordado(): String? {
        return prefs.getString("remember_email", null)
    }

    // Obtener contraseña recordada
    fun obtenerPasswordRecordada(): String? {
        return prefs.getString("remember_password", null)
    }

    // Limpiar credenciales recordadas
    fun limpiarCredencialesRecordadas() {
        prefs.edit {
            remove("remember_email")
            remove("remember_password")
        }
    }

    // Limpiar SOLO la contraseña recordada (mantener email)
    fun limpiarSoloPassword() {
        prefs.edit {
            remove("remember_password")
        }
    }

    fun guardarRememberMe(recordar: Boolean) {
        prefs.edit { putBoolean("remember_me_flag", recordar) }
    }

    fun obtenerRememberMe(): Boolean {
        return prefs.getBoolean("remember_me_flag", false)
    }

}