package com.edurooms.app.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.edurooms.app.R
import com.edurooms.app.data.utils.TokenManager

class PerfilActivity : BaseActivity() {

    private lateinit var tokenManager: TokenManager
    private lateinit var userNameText: TextView
    private lateinit var userEmailText: TextView
    private lateinit var userRoleText: TextView
    private lateinit var userDepartmentText: TextView
    private lateinit var changePasswordButton: Button
    private lateinit var notificationsButton: Button
    private lateinit var logoutButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        // Configurar Toolbar con botón atrás
        setupToolbar(title = "Perfil", showBackButton = true)
        mostrarIconosToolbar(notificaciones = true, perfil = false)

        configurarIconosToolbar(
            onNotificacionesClick = { Toast.makeText(this, "Notificaciones", Toast.LENGTH_SHORT).show() }
        )

        // Configurar Bottom Navigation
        setupBottomNavigation()
        seleccionarItemBottomNav(R.id.nav_perfil)

        // Inicializar TokenManager
        tokenManager = TokenManager(this)

        // Vincular vistas
        userNameText = findViewById(R.id.userNameText)
        userEmailText = findViewById(R.id.userEmailText)
        userRoleText = findViewById(R.id.userRoleText)
        userDepartmentText = findViewById(R.id.userDepartmentText)
        changePasswordButton = findViewById(R.id.changePasswordButton)
        notificationsButton = findViewById(R.id.notificationsButton)
        logoutButton = findViewById(R.id.logoutButton)

        // Cargar datos del usuario
        cargarDatosUsuario()

        // Click listeners
        changePasswordButton.setOnClickListener { irACambiarContrasena() }
        notificationsButton.setOnClickListener { irANotificaciones() }
        logoutButton.setOnClickListener { mostrarDialogoConfirmacionLogout() }
    }

    private fun cargarDatosUsuario() {
        val nombre = tokenManager.obtenerNombre() ?: "Usuario"
        val email = tokenManager.obtenerEmail() ?: ""
        val rol = tokenManager.obtenerRol()
        val departamento = tokenManager.obtenerDepartamento() ?: "No asignado"

        userNameText.text = nombre.ifEmpty { "Usuario" }
        userEmailText.text = email
        userRoleText.text = rol.replaceFirstChar { it.uppercase() }
        userDepartmentText.text = departamento
    }

    private fun irACambiarContrasena() {
        Toast.makeText(this, "Cambiar contraseña - Próximamente", Toast.LENGTH_SHORT).show()
        // TODO: Crear CambiarContraseñaActivity
    }

    private fun irANotificaciones() {
        Toast.makeText(this, "Notificaciones - Próximamente", Toast.LENGTH_SHORT).show()
        // TODO: Crear NotificacionesActivity
    }

    private fun mostrarDialogoConfirmacionLogout() {
        AlertDialog.Builder(this)
            .setTitle("Cerrar Sesión")
            .setMessage("¿Estás seguro que quieres cerrar sesión?")
            .setPositiveButton("Sí, cerrar") { _, _ ->
                cerrarSesion()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }

    private fun cerrarSesion() {
        tokenManager.eliminarToken()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }


}