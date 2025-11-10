package com.edurooms.app.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.edurooms.app.R
import com.edurooms.app.data.utils.TokenManager
import com.google.android.material.bottomnavigation.BottomNavigationView

// MainActivity hereda de BaseActivity para tener Bottom Navigation centralizado
class MainActivity : BaseActivity() {

    private lateinit var tokenManager: TokenManager
    private lateinit var welcomeText: TextView
    private lateinit var verAulasButton: Button
    private lateinit var misReservasButton: Button
    private lateinit var incidenciasButton: Button

    private lateinit var crearAulaButton: Button

    private lateinit var eliminarAulaButton: Button

    private lateinit var gestionarUsuariosButton: Button
    private lateinit var cerrarSesionButton: Button



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Configurar Bottom Navigation desde BaseActivity
        setupBottomNavigation()

        tokenManager = TokenManager(this)

        // Verificar que hay sesión
        if (!tokenManager.tokenValido()) {
            irAlLogin()
            return
        }

        // Vincular vistas
        welcomeText = findViewById(R.id.welcomeText)
        verAulasButton = findViewById(R.id.verAulasButton)
        misReservasButton = findViewById(R.id.misReservasButton)
        incidenciasButton = findViewById(R.id.incidenciasButton)
        crearAulaButton = findViewById(R.id.crearAulaButton)
        eliminarAulaButton = findViewById(R.id.eliminarAulaButton)
        gestionarUsuariosButton = findViewById(R.id.gestionarUsuariosButton)
        cerrarSesionButton = findViewById(R.id.cerrarSesionButton)
        bottomNavigation = findViewById(R.id.bottom_navigation)

        // Mostrar menú según rol
        mostrarMenuSegunRol()

        // Mostrar bienvenida
        welcomeText.text = "Bienvenido a EduRooms"


        // Click listeners
        verAulasButton.setOnClickListener {
            startActivity(Intent(this, ListaAulasActivity::class.java))
            //Toast.makeText(this, "Ver Aulas - Próximamente", Toast.LENGTH_SHORT).show()
        }
        misReservasButton.setOnClickListener {
            Toast.makeText(this, "Mis reservas - Próximamente", Toast.LENGTH_SHORT).show()
        }
        incidenciasButton.setOnClickListener {
           // Toast.makeText(this, "Incidencias - Próximamente", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, IncidenciasActivity::class.java))
        }

        // Click listeners - Botones solo admin
        crearAulaButton.setOnClickListener {
            Toast.makeText(this, "Crear Aula - Próximamente", Toast.LENGTH_SHORT).show()
        }
        eliminarAulaButton.setOnClickListener {
            Toast.makeText(this, "Eliminar Aula - Próximamente", Toast.LENGTH_SHORT).show()
        }
        gestionarUsuariosButton.setOnClickListener {
            Toast.makeText(this, "Gestionar Usuarios - Próximamente", Toast.LENGTH_SHORT).show()
        }

        cerrarSesionButton.setOnClickListener {
            tokenManager.eliminarToken()
            irAlLogin()
        }
    }

    private fun irAlLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun mostrarMenuSegunRol() {
        val rol = tokenManager.obtenerRol()

        android.util.Log.d("MAIN_ACTIVITY", "Rol obtenido: $rol")

        if (rol == "administrador") {
            android.util.Log.d("MAIN_ACTIVITY", "Mostrando botones de ADMIN")
            // Admin ve TODOS los botones
            crearAulaButton.visibility = View.VISIBLE
            eliminarAulaButton.visibility = View.VISIBLE
            gestionarUsuariosButton.visibility = View.VISIBLE
        } else {
            android.util.Log.d("MAIN_ACTIVITY", "Mostrando botones de PROFESOR")
            // Profesor solo ve los botones comunes
            crearAulaButton.visibility = View.GONE
            eliminarAulaButton.visibility = View.GONE
            gestionarUsuariosButton.visibility = View.GONE
        }
    }

}