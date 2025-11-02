package com.edurooms.app.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.edurooms.app.R
import com.edurooms.app.data.utils.TokenManager

class MainActivity : AppCompatActivity() {

    private lateinit var tokenManager: TokenManager
    private lateinit var welcomeText: TextView
    private lateinit var verAulasButton: Button
    private lateinit var misReservasButton: Button
    private lateinit var incidenciasButton: Button
    private lateinit var cerrarSesionButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
        cerrarSesionButton = findViewById(R.id.cerrarSesionButton)

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
        cerrarSesionButton.setOnClickListener {
            tokenManager.eliminarToken()
            irAlLogin()
        }
    }

    private fun irAlLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}