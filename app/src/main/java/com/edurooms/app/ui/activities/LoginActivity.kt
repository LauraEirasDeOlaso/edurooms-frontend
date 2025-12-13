package com.edurooms.app.ui.activities


import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.edurooms.app.R
import com.edurooms.app.data.models.LoginRequest
import com.edurooms.app.data.network.RetrofitClient
import com.edurooms.app.data.utils.TokenManager
import com.edurooms.app.ui.activities.BaseActivity.Companion.configurarPasswordToggle
import kotlinx.coroutines.launch



class LoginActivity : AppCompatActivity() {

    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var loginButton: Button
    private lateinit var tokenManager: TokenManager
    private lateinit var recuperarPasswordButton: TextView
    private lateinit var passwordToggle: ImageView

    private lateinit var rememberMeCheckbox: CheckBox



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
        recuperarPasswordButton = findViewById(R.id.recuperarPasswordButton)
        passwordToggle = findViewById(R.id.passwordToggle)
        rememberMeCheckbox = findViewById(R.id.rememberMeCheckbox)

        configurarPasswordToggle(passwordToggle, passwordInput)



        // Cargar credenciales si existen
        val emailRecordado = tokenManager.obtenerEmailRecordado()
        val passwordRecordada = tokenManager.obtenerPasswordRecordada()

        if (!emailRecordado.isNullOrEmpty() && !passwordRecordada.isNullOrEmpty()) {
            emailInput.setText(emailRecordado)
            passwordInput.setText(passwordRecordada)
            rememberMeCheckbox.isChecked = true
        }

        // Ir al login con los datos precargados:
        // Obtener datos del Intent (si vienen del registro)
        val emailDelRegistro = intent.getStringExtra("email") ?: ""
        if (emailDelRegistro.isNotEmpty()) {
            emailInput.setText(emailDelRegistro)
        }

        // Click listeners
        loginButton.setOnClickListener { realizarLogin() }
        recuperarPasswordButton.setOnClickListener { irARecuperarPassword() }

        // ← CAMBIAR DE OnClickListener A OnTouchListener

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
                    tokenManager.guardarNombre(loginResponse.usuario.nombre)
                    tokenManager.guardarEmail(loginResponse.usuario.email)
                    tokenManager.guardarRol(loginResponse.usuario.rol)
                    tokenManager.guardarIdUsuario(loginResponse.usuario.id)
                    tokenManager.guardarPrimeraVezLogin(loginResponse.usuario.primera_vez_login)

                    // Guardar o limpiar credenciales según checkbox
                    if (rememberMeCheckbox.isChecked) {
                        tokenManager.guardarCredencialesRecordadas(email, password)
                    } else {
                        tokenManager.limpiarCredencialesRecordadas()
                    }


                    // Mensaje de éxito
                    Toast.makeText(this@LoginActivity, "✅ Login exitoso", Toast.LENGTH_SHORT).show()

                    android.util.Log.d(
                        "LOGIN",
                        "Primera vez guardada: ${tokenManager.obtenerPrimeraVezLogin()}"
                    )

                    // Detectar si es primera vez con contraseña temporal
                    if (loginResponse.usuario.primera_vez_login) {
                        val intent = Intent(this@LoginActivity, CambiarPasswordActivity::class.java)
                        intent.putExtra("es_primera_vez", true)
                        startActivity(intent)
                        finish()
                    } else {
                        // Ir al menú principal normalmente
                        irAlMenu()
                    }
                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        "❌ Credenciales inválidas",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                android.util.Log.e("LOGIN", "Exception: ${e.message}", e)
                Toast.makeText(this@LoginActivity, "❌ Error: ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun irAlMenu() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun irARecuperarPassword() {
        val email = emailInput.text.toString().trim()
        val intent = Intent(this, RecuperarPasswordActivity::class.java)
        intent.putExtra("email", email)
        startActivity(intent)
    }



}