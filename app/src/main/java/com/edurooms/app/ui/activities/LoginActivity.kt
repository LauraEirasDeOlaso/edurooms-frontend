package com.edurooms.app.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.edurooms.app.R
import com.edurooms.app.data.models.LoginRequest
import com.edurooms.app.data.network.RetrofitClient
import com.edurooms.app.data.utils.TokenManager
import kotlinx.coroutines.launch


class LoginActivity : AppCompatActivity() {

    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var loginButton: Button
    private lateinit var registerButton: Button
    private lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Inicializar TokenManager
        tokenManager = TokenManager(this)

        // Verificar si ya hay sesión activa
        if (tokenManager.tokenValido()) {
            irAlMenu()
            return
        }

        // Vincular vistas
        emailInput = findViewById(R.id.emailInput)
        passwordInput = findViewById(R.id.passwordInput)
        loginButton = findViewById(R.id.loginButton)
        registerButton = findViewById(R.id.registerButton)

        // Ir al login con los datos precargados:
        // Obtener datos del Intent (si vienen del registro)
        val emailDelRegistro = intent.getStringExtra("email") ?: ""
        if (emailDelRegistro.isNotEmpty()) {
            emailInput.setText(emailDelRegistro)
        }

            // Click listeners
        loginButton.setOnClickListener { realizarLogin() }
        registerButton.setOnClickListener { irARegistro() }
    }

    private fun realizarLogin() {
        val email = emailInput.text.toString().trim()
        val password = passwordInput.text.toString().trim()

        // Validar campos
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        // Realizar login
        lifecycleScope.launch {
            try {
                val loginRequest = LoginRequest(email, password)
                val response = RetrofitClient.apiService.login(loginRequest)

                if (response.isSuccessful && response.body() != null) {
                    val loginResponse = response.body()!!

                    android.util.Log.d("LOGIN", "Respuesta completa: $loginResponse")
                    android.util.Log.d("LOGIN", "Usuario: ${loginResponse.usuario}")
                    android.util.Log.d("LOGIN", "Rol recibido: ${loginResponse.usuario.rol}")

                    // Guardar token
                    tokenManager.guardarToken(loginResponse.token)

                    // AGREGAR ESTO - Guardar rol
                    tokenManager.guardarRol(loginResponse.usuario.rol)

                    // Mensaje de éxito
                    Toast.makeText(this@LoginActivity, "✅ Login exitoso", Toast.LENGTH_SHORT).show()

                    // Ir al menú principal
                    irAlMenu()
                } else {
                    Toast.makeText(this@LoginActivity, "❌ Credenciales inválidas", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                android.util.Log.e("LOGIN", "Exception: ${e.message}", e)
                Toast.makeText(this@LoginActivity, "❌ Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun irAlMenu() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun irARegistro() {
        startActivity(Intent(this, RegisterActivity::class.java))
       // Toast.makeText(this, "Registro aún no disponible", Toast.LENGTH_SHORT).show()
    }
}