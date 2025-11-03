package com.edurooms.app.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.edurooms.app.R
import com.edurooms.app.data.models.RegistroRequest
import com.edurooms.app.data.network.RetrofitClient
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var nombreInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var confirmarPasswordInput: EditText
    private lateinit var rolSpinner: Spinner
    private lateinit var crearCuentaButton: Button
    private lateinit var irALoginButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Vincular vistas
        nombreInput = findViewById(R.id.nombreInput)
        emailInput = findViewById(R.id.emailInput)
        passwordInput = findViewById(R.id.passwordInput)
        confirmarPasswordInput = findViewById(R.id.confirmarPasswordInput)
        rolSpinner = findViewById(R.id.rolSpinner)
        crearCuentaButton = findViewById(R.id.crearCuentaButton)
        irALoginButton = findViewById(R.id.irALoginButton)

        // Click listeners
        crearCuentaButton.setOnClickListener { realizarRegistro() }
        irALoginButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun realizarRegistro() {
        val nombre = nombreInput.text.toString().trim()
        val email = emailInput.text.toString().trim()
        val password = passwordInput.text.toString().trim()
        val confirmarPassword = confirmarPasswordInput.text.toString().trim()
        val rol = rolSpinner.selectedItem.toString()

        // Validar campos
        if (nombre.isEmpty() || email.isEmpty() || password.isEmpty() || confirmarPassword.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != confirmarPassword) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            return
        }

        if (password.length < 6) {
            Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
            return
        }

        // Realizar registro
        lifecycleScope.launch {
            try {
                val registroRequest = RegistroRequest(nombre, email, password, confirmarPassword, rol)
                android.util.Log.d("REGISTRO", "Enviando: $registroRequest")

                val response = RetrofitClient.apiService.registro(registroRequest)

                android.util.Log.d("REGISTRO", "Response Code: ${response.code()}")
                android.util.Log.d("REGISTRO", "Is Successful: ${response.isSuccessful}")
                android.util.Log.d("REGISTRO", "Body: ${response.body()}")
                android.util.Log.d("REGISTRO", "Error: ${response.errorBody()?.string()}")

                if (response.isSuccessful && response.body() != null) {
                    Toast.makeText(this@RegisterActivity, "✅ Cuenta creada exitosamente", Toast.LENGTH_SHORT).show()

                    // Ir a login CON DATOS PRECARGADOS
                    val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                    intent.putExtra("email", email)
                    startActivity(intent)
                    finish()
                } else {
                    try {
                        val errorBody = response.errorBody()?.string()
                        val errorJson = org.json.JSONObject(errorBody ?: "{}")
                        val errorMsg = errorJson.optString("mensaje", "Error desconocido")
                        Toast.makeText(this@RegisterActivity, "❌ $errorMsg", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        Toast.makeText(this@RegisterActivity, "❌ Error ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("REGISTRO", "Exception: ${e.message}", e)
                Toast.makeText(this@RegisterActivity, "❌ Exception: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}