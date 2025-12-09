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

class RecuperarPasswordActivity : AppCompatActivity() {

    private lateinit var emailInput: EditText
    private lateinit var sendButton: Button
    private lateinit var backButton: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recuperar_password)

        // Vincular vistas
        emailInput = findViewById(R.id.emailInput)
        sendButton = findViewById(R.id.sendButton)
        backButton = findViewById(R.id.backButton)

        // Obtener email del Intent (si viene del login)
        val emailDelLogin = intent.getStringExtra("email") ?: ""
        if (emailDelLogin.isNotEmpty()) {
            emailInput.setText(emailDelLogin)
        }

        // Click listeners
        sendButton.setOnClickListener { validarEmail() }
        backButton.setOnClickListener { volverAlLogin() }
    }

    private fun validarEmail() {
        val email = emailInput.text.toString().trim()

        if (email.isEmpty()) {
            Toast.makeText(this, "❌ Ingresa tu email", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                android.util.Log.d("RECUPERAR_PASSWORD", "Validando email: $email")
                val response = RetrofitClient.apiService.validarEmailExiste(email)

                android.util.Log.d("RECUPERAR_PASSWORD", "Response code: ${response.code()}")

                if (response.isSuccessful && response.body() != null) {
                    if (response.body()!!.existe == true) {
                        mostrarMensajeExito(email)
                    } else {
                        Toast.makeText(
                            this@RecuperarPasswordActivity,
                            "❌ Este email no está registrado",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@RecuperarPasswordActivity,
                        "❌ Error al verificar email",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                android.util.Log.e("RECUPERAR_PASSWORD", "Exception: ${e.message}", e)
                Toast.makeText(
                    this@RecuperarPasswordActivity,
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