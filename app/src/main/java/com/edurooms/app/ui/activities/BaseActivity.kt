package com.edurooms.app.ui.activities


import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.edurooms.app.R
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.widget.AppCompatImageButton
import android.view.View
import android.widget.ImageView
import android.view.MotionEvent
import android.widget.EditText

// BaseActivity: Clase base para todas las Activities que TENDRÁN Bottom Navigation
// Proporciona la lógica centralizada de navegación
open class BaseActivity : AppCompatActivity() {

    // Referencia al Bottom Navigation (protegida para que las clases hijas la accedan)
    protected lateinit var bottomNavigation: BottomNavigationView
    protected lateinit var toolbar: Toolbar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Las Activities hijas llaman a setContentView primero
        // Luego llaman a setupBottomNavigation()
    }

    // Método que las Activities hijas llaman DESPUÉS de setContentView
    // Esto configura el Toolbar
    protected fun setupToolbar(title: String = "", showBackButton: Boolean = false) {
        try {
            // Obtener referencia del Toolbar desde el layout
            toolbar = findViewById(R.id.toolbar)
            toolbar.title = title
            setSupportActionBar(toolbar)
            // Mostrar flecha atrás si es necesario
            if (showBackButton) {
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
            }else {
                // Esconder la flecha si es false
                toolbar.navigationIcon = null
            }

        } catch (_: Exception) {
            android.util.Log.d("BASE_ACTIVITY", "Toolbar no encontrada en este layout")
        }
    }

        // Método que las Activities hijas llaman DESPUÉS de setContentView
    // Esto configura el Bottom Navigation
    protected fun setupBottomNavigation() {
        try {
            // Obtener referencia del Bottom Navigation desde el layout
            bottomNavigation = findViewById(R.id.bottom_navigation)

            // Configurar los listeners (qué pasa cuando toca cada icono)
            configurarBottomNavigation()
        } catch (_: Exception) {
            // Si el layout no tiene Bottom Nav, no crashea
            android.util.Log.d("BASE_ACTIVITY", "Bottom Navigation no encontrada en este layout")
        }
    }

    // Configurar qué pasa cuando toca cada icono del Bottom Navigation
    private fun configurarBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                // Icono Inicio (casa)
                R.id.nav_home -> {
                    if (this !is MainActivity) {
                        // Si NO estoy en MainActivity, voy a MainActivity
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                    true
                }
                // Icono Aulas (vista)
                R.id.nav_aulas -> {
                    if (this !is ListaAulasActivity) {
                        // Si NO estoy en ListaAulasActivity, voy a ListaAulasActivity
                        startActivity(Intent(this, ListaAulasActivity::class.java))
                        finish()
                    }
                    true
                }
                // Icono Mis Reservas (calendario)
                R.id.nav_reservas -> {
                    if (this !is MisReservasActivity) {
                        startActivity(Intent(this, MisReservasActivity::class.java))
                        finish()
                    }
                    true
                }
                // Icono Incidencias (alerta)
                R.id.nav_incidencias -> {
                    if (this !is IncidenciasActivity) {
                        // Si NO estoy en IncidenciasActivity, voy a IncidenciasActivity
                        startActivity(Intent(this, IncidenciasActivity::class.java))
                        finish()
                    }
                    true
                }
                // Icono Perfil (persona)
                R.id.nav_perfil -> {
                    if (this !is PerfilActivity) {
                        startActivity(Intent(this, PerfilActivity::class.java))
                    }
                    true
                }
                else -> false
            }
        }
    }
    // Método para marcar cuál icono está activo según la Activity
    protected fun seleccionarItemBottomNav(itemId: Int) {
        bottomNavigation.selectedItemId = itemId
    }

    protected fun mostrarIconosToolbar(notificaciones: Boolean = false, perfil: Boolean = false) {
        val notificationIcon = toolbar.findViewById<AppCompatImageButton>(R.id.notificationIcon)
        val profileIcon = toolbar.findViewById<AppCompatImageButton>(R.id.profileIcon)

        notificationIcon.visibility = if (notificaciones) View.VISIBLE else View.GONE
        profileIcon.visibility = if (perfil) View.VISIBLE else View.GONE
    }

    protected fun configurarIconosToolbar(
        onNotificacionesClick: (() -> Unit)? = null,
        onPerfilClick: (() -> Unit)? = null
    ) {
        val notificationIcon = toolbar.findViewById<AppCompatImageButton>(R.id.notificationIcon)
        val profileIcon = toolbar.findViewById<AppCompatImageButton>(R.id.profileIcon)

        notificationIcon.setOnClickListener { onNotificacionesClick?.invoke() }
        profileIcon.setOnClickListener { onPerfilClick?.invoke() }
    }

    // Override para que el botón atrás del Toolbar funcione
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    /**
     * Configura el toggle de visibilidad de contraseña (ojito)
     *
     * Funcionalidad:
     * - Hold (mantener presionado): muestra la contraseña
     * - Release (soltar): oculta la contraseña
     *
     * Se reutiliza en múltiples Activities: LoginActivity, CambiarPasswordActivity, CrearUsuarioActivity
     *
     * @param passwordToggle ImageView del icono de visibilidad
     * @param passwordInput EditText donde está la contraseña
     */
    companion object {
        @Suppress("ClickableViewAccessibility")
        fun configurarPasswordToggle(
            passwordToggle: ImageView,
            passwordInput: EditText
        ) {
            passwordToggle.setOnTouchListener { _, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        passwordInput.inputType = android.text.InputType.TYPE_CLASS_TEXT
                        passwordToggle.setImageResource(R.drawable.ic_visibility)
                        passwordInput.setSelection(passwordInput.text.length)
                    }
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        passwordInput.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
                        passwordToggle.setImageResource(R.drawable.ic_visibility_off)
                        passwordInput.setSelection(passwordInput.text.length)
                    }
                    else -> {}
                }
                true
            }
        }
    }
}