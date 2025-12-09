package com.edurooms.app.ui.activities

import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import com.edurooms.app.R
import com.edurooms.app.data.models.CrearUsuarioRequest
import com.edurooms.app.data.network.RetrofitClient
import com.edurooms.app.data.utils.TokenManager
import kotlinx.coroutines.launch


class CrearUsuarioActivity : BaseActivity() {

    private lateinit var nombreInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var departamentoInput: EditText
    private lateinit var crearUsuarioButton: Button
    private lateinit var resultadoCard: CardView
    private lateinit var passwordTemporalText: TextView
    private lateinit var copiarPasswordButton: Button
    private lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_usuario)

        // Configurar Toolbar
        setupToolbar(title = "Crear Usuario", showBackButton = true)

        // Configurar Bottom Navigation
        setupBottomNavigation()

        // Inicializar TokenManager
        tokenManager = TokenManager(this)

        // Vincular vistas
        nombreInput = findViewById(R.id.nombreInput)
        emailInput = findViewById(R.id.emailInput)
        departamentoInput = findViewById(R.id.departamentoInput)
        crearUsuarioButton = findViewById(R.id.crearUsuarioButton)
        resultadoCard = findViewById(R.id.resultadoCard)
        passwordTemporalText = findViewById(R.id.passwordTemporalText)
        copiarPasswordButton = findViewById(R.id.copiarPasswordButton)

        // Click listeners
        crearUsuarioButton.setOnClickListener { crearNuevoUsuario() }
        copiarPasswordButton.setOnClickListener { copiarAlPortapapeles() }
    }

    private fun crearNuevoUsuario() {
        val nombre = nombreInput.text.toString().trim()
        val email = emailInput.text.toString().trim()
        val departamento = departamentoInput.text.toString().trim()

        // Validar campos
        if (nombre.isEmpty() || email.isEmpty() || departamento.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "El email no es válido", Toast.LENGTH_SHORT).show()
            return
        }

        // Realizar llamada al backend
        lifecycleScope.launch {
            try {
                val token = tokenManager.obtenerToken() ?: ""
                val crearUsuarioRequest = CrearUsuarioRequest(nombre, email, departamento)

                android.util.Log.d("CREAR_USUARIO", "Enviando: $crearUsuarioRequest")

                val response = RetrofitClient.apiService.crearUsuario(
                    "Bearer $token",
                    crearUsuarioRequest
                )

                android.util.Log.d("CREAR_USUARIO", "Response Code: ${response.code()}")
                android.util.Log.d("CREAR_USUARIO", "Is Successful: ${response.isSuccessful}")

                if (response.isSuccessful && response.body() != null) {
                    val resultado = response.body()!!

                    // Mostrar password temporal
                    mostrarResultado(resultado.passwordTemporal)

                    // Limpiar campos
                    nombreInput.text.clear()
                    emailInput.text.clear()
                    departamentoInput.text.clear()

                    Toast.makeText(
                        this@CrearUsuarioActivity,
                        "✅ Usuario creado exitosamente",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    try {
                        val errorBody = response.errorBody()?.string()
                        val errorJson = org.json.JSONObject(errorBody ?: "{}")
                        val errorMsg = errorJson.optString("mensaje", "Error desconocido")
                        Toast.makeText(this@CrearUsuarioActivity, "❌ $errorMsg", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        Toast.makeText(
                            this@CrearUsuarioActivity,
                            "❌ Error ${response.code()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("CREAR_USUARIO", "Exception: ${e.message}", e)
                Toast.makeText(
                    this@CrearUsuarioActivity,
                    "❌ Error: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun mostrarResultado(passwordTemporal: String) {
        passwordTemporalText.text = passwordTemporal
        resultadoCard.visibility = android.view.View.VISIBLE
    }

    private fun copiarAlPortapapeles() {
        val password = passwordTemporalText.text.toString()
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = android.content.ClipData.newPlainText("password", password)
        clipboard.setPrimaryClip(clip)

        Toast.makeText(this, "✅ Contraseña copiada al portapapeles", Toast.LENGTH_SHORT).show()
    }
}