package com.edurooms.app.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.edurooms.app.R
import com.edurooms.app.data.network.RetrofitClient
import kotlinx.coroutines.launch

class RecuperarContraseñaActivity : AppCompatActivity() {

    private lateinit var emailInput: EditText
    private lateinit var sendButton: Button
    private lateinit var backButton: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recuperar_contraseña)

        // Vincular vistas
        emailInput = findViewById(R.id.emailInput)
        sendButton = findViewById(R.id.sendButton)
        backButton = findViewById(R.id.backButton)

        // Click listeners
        sendButton.setOnClickListener { validarEmail() }
        backButton.setOnClickListener { volverAlLogin() }
    }

    private fun validarEmail() {
        val email = emailInput.text.toString().trim()

        // Validar que el email no esté vacío
        if (email.isEmpty()) {
            Toast.makeText(this, "❌ Ingresa tu email", Toast.LENGTH_SHORT).show()
            return
        }

        // Validar formato de email
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "❌ Formato de email inválido", Toast.LENGTH_SHORT).show()
            return
        }

        // Verificar que el email exista en el sistema
        lifecycleScope.launch {
            try {
                // Usamos obtenerUsuarios para verificar si el email existe
                val response = RetrofitClient.apiService.obtenerUsuarios()

                if (response.isSuccessful && response.body() != null) {
                    val usuarios = response.body()!!
                    val usuarioExiste = usuarios.any { it.email == email }

                    if (usuarioExiste) {
                        // Email existe, mostrar mensaje
                        mostrarMensajeExito(email)
                    } else {
                        Toast.makeText(
                            this@RecuperarContraseñaActivity,
                            "❌ Este email no está registrado",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@RecuperarContraseñaActivity,
                        "❌ Error al verificar el email",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                android.util.Log.e("RECUPERAR_CONTRASEÑA", "Exception: ${e.message}", e)
                Toast.makeText(
                    this@RecuperarContraseñaActivity,
                    "❌ Error: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun mostrarMensajeExito(email: String) {
        Toast.makeText(
            this,
            "✅ Instrucciones enviadas a $email\n\nContacta al administrador si no las recibiste",
            Toast.LENGTH_LONG
        ).show()

        // Limpiar campo y volver al login después de 2 segundos
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            volverAlLogin()
        }, 2000)
    }

    private fun volverAlLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}