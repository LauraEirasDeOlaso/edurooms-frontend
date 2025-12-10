package com.edurooms.app.ui.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.edurooms.app.R
import com.edurooms.app.data.models.CambiarPasswordRequest
import com.edurooms.app.data.utils.TokenManager
import com.edurooms.app.data.network.RetrofitClient
import kotlinx.coroutines.launch


class CambiarPasswordActivity : BaseActivity() {

    private lateinit var tokenManager: TokenManager
    private lateinit var passwordActualContainer: LinearLayout
    private lateinit var passwordActualInput: EditText
    private lateinit var passwordNuevaInput: EditText
    private lateinit var passwordConfirmarInput: EditText
    private lateinit var guardarPasswordButton: Button

    // TextViews de requisitos
    private lateinit var req_minimo8: TextView
    private lateinit var req_mayuscula: TextView
    private lateinit var req_minuscula: TextView
    private lateinit var req_numero: TextView
    private lateinit var req_especial: TextView

    private var esPrimeraVez: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cambiar_password)

        // Configurar Toolbar
        setupToolbar(title = "Cambiar Contraseña", showBackButton = true)
        mostrarIconosToolbar(notificaciones = true, perfil = true)
        configurarIconosToolbar(
            onNotificacionesClick = { Toast.makeText(this, "Notificaciones", Toast.LENGTH_SHORT).show() },
            onPerfilClick = { startActivity(Intent(this, PerfilActivity::class.java)) }
        )

        // Inicializar TokenManager
        tokenManager = TokenManager(this)

        // Vincular vistas
        passwordActualContainer = findViewById(R.id.passwordActualContainer)
        passwordActualInput = findViewById(R.id.passwordActualInput)
        passwordNuevaInput = findViewById(R.id.passwordNuevaInput)
        passwordConfirmarInput = findViewById(R.id.passwordConfirmarInput)
        guardarPasswordButton = findViewById(R.id.guardarPasswordButton)


        // Vincular TextViews de requisitos
        req_minimo8 = findViewById(R.id.req_minimo8)
        req_mayuscula = findViewById(R.id.req_mayuscula)
        req_minuscula = findViewById(R.id.req_minuscula)
        req_numero = findViewById(R.id.req_numero)
        req_especial = findViewById(R.id.req_especial)

        // Vincular ImageView toggles
        val passwordActualToggle = findViewById<ImageView>(R.id.passwordActualToggle)
        val passwordNuevaToggle = findViewById<ImageView>(R.id.passwordNuevaToggle)
        val passwordConfirmarToggle = findViewById<ImageView>(R.id.passwordConfirmarToggle)

        // Configurar toggles
        configurarPasswordToggle(passwordActualToggle, passwordActualInput)
        configurarPasswordToggle(passwordNuevaToggle, passwordNuevaInput)
        configurarPasswordToggle(passwordConfirmarToggle, passwordConfirmarInput)

        // Detectar si es primera vez o cambio normal
        esPrimeraVez = intent.getBooleanExtra("es_primera_vez", false)

        // Si es primera vez, ocultar campo de contraseña actual
        if (esPrimeraVez) {
            passwordActualContainer.visibility = android.view.View.GONE
        } else {
            passwordActualContainer.visibility = android.view.View.VISIBLE
        }

        // Click listener
        guardarPasswordButton.setOnClickListener { cambiarContraseña() }

        // ← AGREGAR ESTO: Validación en tiempo real
        passwordNuevaInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                validarRequisitosEnTiempoReal(s.toString())
            }
        })
    }

    private fun validarRequisitosEnTiempoReal(password: String) {
        val colorValido = Color.parseColor("#4CAF50")  // Verde
        val colorInvalido = Color.parseColor("#F44336")  // Rojo

        // Validar cada requisito
        val tiene8 = password.length >= 8
        req_minimo8.setTextColor(if (tiene8) colorValido else colorInvalido)

        val tieneMay = password.any { it.isUpperCase() }
        req_mayuscula.setTextColor(if (tieneMay) colorValido else colorInvalido)

        val tieneMin = password.any { it.isLowerCase() }
        req_minuscula.setTextColor(if (tieneMin) colorValido else colorInvalido)

        val tieneNum = password.any { it.isDigit() }
        req_numero.setTextColor(if (tieneNum) colorValido else colorInvalido)

        val caracteresEspeciales = "@#\$%!&*"
        val tieneEsp = password.any { it in caracteresEspeciales }
        req_especial.setTextColor(if (tieneEsp) colorValido else colorInvalido)
    }

    private fun cambiarContraseña() {
        val passwordNueva = passwordNuevaInput.text.toString().trim()
        val passwordConfirmar = passwordConfirmarInput.text.toString().trim()
        val passwordActual = if (!esPrimeraVez) {
            passwordActualInput.text.toString().trim()
        } else {
            "" // No se necesita para primera vez
        }

        // Validar que no estén vacías
        if (passwordNueva.isEmpty() || passwordConfirmar.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        if (!esPrimeraVez && passwordActual.isEmpty()) {
            Toast.makeText(this, "Ingresa tu contraseña actual", Toast.LENGTH_SHORT).show()
            return
        }

        // Validar que coincidan
        if (passwordNueva != passwordConfirmar) {
            Toast.makeText(this, "Las contraseñas nuevas no coinciden", Toast.LENGTH_SHORT).show()
            return
        }

        // Validar requisitos
        if (!validarRequisitosContraseña(passwordNueva)) {
            Toast.makeText(
                this,
                "La contraseña debe tener:\n" +
                        "• Mínimo 8 caracteres\n" +
                        "• Al menos 1 mayúscula\n" +
                        "• Al menos 1 minúscula\n" +
                        "• Al menos 1 número\n" +
                        "• Al menos 1 carácter especial",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        // Llamar al backend
        lifecycleScope.launch {
            try {
                val token = tokenManager.obtenerToken() ?: ""
                val usuarioIdActual = tokenManager.obtenerIdUsuario()

                // Crear request
                val requestBody = CambiarPasswordRequest(
                    passwordActual = passwordActual,
                    passwordNueva = passwordNueva,
                    passwordNuevaConfirmar =  passwordConfirmar,
                    esPrimeraVez = esPrimeraVez
                )

                val response = RetrofitClient.apiService.cambiarPassword(
                    "Bearer $token",
                    usuarioIdActual,
                    requestBody
                )

                if (response.isSuccessful) {
                    Toast.makeText(this@CambiarPasswordActivity, "✅ Contraseña cambiada exitosamente", Toast.LENGTH_SHORT).show()

                    // Actualizar flag de primera vez
                    tokenManager.guardarPrimeraVezLogin(false)

                    // Si era primera vez, ir a MainActivity
                    // Si no, solo cerrar esta activity
                    if (esPrimeraVez) {
                        startActivity(Intent(this@CambiarPasswordActivity, MainActivity::class.java))
                        finish()
                    } else {
                        finish()
                    }
                } else {
                    try {
                        val errorBody = response.errorBody()?.string()
                        val errorJson = org.json.JSONObject(errorBody ?: "{}")
                        val errorMsg = errorJson.optString("mensaje", "Error desconocido")
                        Toast.makeText(this@CambiarPasswordActivity, "❌ $errorMsg", Toast.LENGTH_SHORT).show()
                    } catch (_: Exception) {
                        Toast.makeText(this@CambiarPasswordActivity, "❌ Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(this@CambiarPasswordActivity, "❌ Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validarRequisitosContraseña(password: String): Boolean {
        // Mínimo 8 caracteres
        if (password.length < 8) return false

        // Al menos 1 mayúscula
        if (!password.any { it.isUpperCase() }) return false

        // Al menos 1 minúscula
        if (!password.any { it.isLowerCase() }) return false

        // Al menos 1 número
        if (!password.any { it.isDigit() }) return false

        // Al menos 1 carácter especial
        val caracteresEspeciales = "@#\$%!&*"
        if (!password.any { it in caracteresEspeciales }) return false

        return true
    }

    private fun configurarPasswordToggle(toggleIcon: ImageView, passwordInput: EditText) {
        var mostrandoPassword = false

        toggleIcon.setOnClickListener {
            mostrandoPassword = !mostrandoPassword

            if (mostrandoPassword) {
                // Mostrar contraseña
                passwordInput.inputType = android.text.InputType.TYPE_CLASS_TEXT
                toggleIcon.setImageResource(android.R.drawable.ic_menu_view) // Ojo abierto
            } else {
                // Ocultar contraseña
                passwordInput.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
                toggleIcon.setImageResource(android.R.drawable.ic_secure) // Ojo cerrado
            }

            // Mantener el cursor al final
            passwordInput.setSelection(passwordInput.text.length)
        }
    }
}