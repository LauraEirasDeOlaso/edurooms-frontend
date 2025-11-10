package com.edurooms.app.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.edurooms.app.R

// BaseActivity: Clase base para todas las Activities que TENDRÁN Bottom Navigation
// Proporciona la lógica centralizada de navegación
open class BaseActivity : AppCompatActivity() {

    // Referencia al Bottom Navigation (protegida para que las clases hijas la accedan)
    protected lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Las Activities hijas llaman a setContentView primero
        // Luego llaman a setupBottomNavigation()
    }

    // Método que las Activities hijas llaman DESPUÉS de setContentView
    // Esto configura el Bottom Navigation
    protected fun setupBottomNavigation() {
        try {
            // Obtener referencia del Bottom Navigation desde el layout
            bottomNavigation = findViewById(R.id.bottom_navigation)

            // Configurar los listeners (qué pasa cuando toca cada icono)
            configurarBottomNavigation()
        } catch (e: Exception) {
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
                    Toast.makeText(this, "Mis Reservas - Próximamente", Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(this, "Perfil - Próximamente", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
    }
}