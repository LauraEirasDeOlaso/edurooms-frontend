package com.edurooms.app.ui.activities

import android.content.Intent
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
import androidx.core.graphics.toColorInt


class CambiarPasswordActivity : BaseActivity() {

    private lateinit var tokenManager: TokenManager
    private lateinit var passwordActualContainer: LinearLayout
    private lateinit var passwordActualInput: EditText
    private lateinit var passwordNuevaInput: EditText
    private lateinit var passwordConfirmarInput: EditText
    private lateinit var guardarPasswordButton: Button

    // TextViews de requisitos
    private lateinit var reqMinimo8: TextView
    private lateinit var reqMayuscula: TextView
    private lateinit var reqMinuscula: TextView
    private lateinit var reqNumero: TextView
    private lateinit var reqEspecial: TextView

    private var esPrimeraVez: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cambiar_password)

        // Inicializar TokenManager
        tokenManager = TokenManager(this)

        // Detectar si es primera vez PRIMERO
        esPrimeraVez = intent.getBooleanExtra("es_primera_vez", false)
        android.util.Log.d("CAMBIAR_PWD", "esPrimeraVez = $esPrimeraVez")

        // Configurar Toolbar
        setupToolbar(title = "", showBackButton = true)
        mostrarIconosToolbar(notificaciones = !esPrimeraVez, perfil = !esPrimeraVez)

        // Solo configurar iconos si NO es primera vez
        if (!esPrimeraVez) {
            configurarIconosToolbar(
                onNotificacionesClick = { Toast.makeText(this, "Notificaciones", Toast.LENGTH_SHORT).show() },
                onPerfilClick = { startActivity(Intent(this, PerfilActivity::class.java)) }
            )
        }

        // Vincular vistas
        passwordActualContainer = findViewById(R.id.passwordActualContainer)
        passwordActualInput = findViewById(R.id.passwordActualInput)
        passwordNuevaInput = findViewById(R.id.passwordNuevaInput)
        passwordConfirmarInput = findViewById(R.id.passwordConfirmarInput)
        guardarPasswordButton = findViewById(R.id.guardarPasswordButton)

        // Vincular TextViews de requisitos
        reqMinimo8 = findViewById(R.id.req_minimo8)
        reqMayuscula = findViewById(R.id.req_mayuscula)
        reqMinuscula = findViewById(R.id.req_minuscula)
        reqNumero = findViewById(R.id.req_numero)
        reqEspecial = findViewById(R.id.req_especial)

        // Vincular ImageView toggles
        val passwordActualToggle = findViewById<ImageView>(R.id.passwordActualToggle)
        val passwordNuevaToggle = findViewById<ImageView>(R.id.passwordNuevaToggle)
        val passwordConfirmarToggle = findViewById<ImageView>(R.id.passwordConfirmarToggle)

        // Configurar toggles
        configurarPasswordToggle(passwordActualToggle, passwordActualInput)
        configurarPasswordToggle(passwordNuevaToggle, passwordNuevaInput)
        configurarPasswordToggle(passwordConfirmarToggle, passwordConfirmarInput)

        // Si es primera vez, desactivar botón atrás
        if (esPrimeraVez) {
            supportActionBar?.setDisplayHomeAsUpEnabled(false)

            onBackPressedDispatcher.addCallback(this, object : androidx.activity.OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    tokenManager.eliminarToken()
                    tokenManager.limpiarCredencialesRecordadas()
                    startActivity(Intent(this@CambiarPasswordActivity, LoginActivity::class.java))
                    finish()
                }
            })
        }

        // SIEMPRE mostrar el campo de contraseña actual
        passwordActualContainer.visibility = android.view.View.VISIBLE

        // Click listener
        guardarPasswordButton.setOnClickListener { cambiarContrasena() }

        // Validación en tiempo real
        passwordNuevaInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                validarRequisitosEnTiempoReal(s.toString())
            }
        })
    }

    private fun validarRequisitosEnTiempoReal(password: String) {
        val colorValido = ("#4CAF50").toColorInt()  // Verde
        val colorInvalido = ("#F44336").toColorInt()  // Rojo

        // Validar cada requisito
        val tiene8 = password.length >= 8
        reqMinimo8.setTextColor(if (tiene8) colorValido else colorInvalido)

        val tieneMay = password.any { it.isUpperCase() }
        reqMayuscula.setTextColor(if (tieneMay) colorValido else colorInvalido)

        val tieneMin = password.any { it.isLowerCase() }
        reqMinuscula.setTextColor(if (tieneMin) colorValido else colorInvalido)

        val tieneNum = password.any { it.isDigit() }
        reqNumero.setTextColor(if (tieneNum) colorValido else colorInvalido)

        val caracteresEspeciales = "@#$%!&*"
        val tieneEsp = password.any { it in caracteresEspeciales }
        reqEspecial.setTextColor(if (tieneEsp) colorValido else colorInvalido)
    }

    private fun cambiarContrasena() {
        val passwordNueva = passwordNuevaInput.text.toString().trim()
        val passwordConfirmar = passwordConfirmarInput.text.toString().trim()
        val passwordActual = passwordActualInput.text.toString().trim()

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
        if (!validarRequisitosContrasena(passwordNueva)) {
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
                    if (esPrimeraVez) {
                        // PRIMERA VEZ: Limpiar todo y volver a MainActivity
                        tokenManager.limpiarCredencialesRecordadas()
                        startActivity(Intent(this@CambiarPasswordActivity, MainActivity::class.java))
                        finish()
                    } else {
                        // DESDE PERFIL: Eliminar token, limpiar SOLO password y volver a LoginActivity
                        android.util.Log.d("CAMBIAR_PWD", "DESDE PERFIL - Limpiando solo password")
                        tokenManager.eliminarToken()
                        tokenManager.limpiarSoloPassword()
                        //Comienza de nuevo
                        val intent = Intent(this@CambiarPasswordActivity, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
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

    private fun validarRequisitosContrasena(password: String): Boolean {
        // Mínimo 8 caracteres
        if (password.length < 8) return false

        // Al menos 1 mayúscula
        if (!password.any { it.isUpperCase() }) return false

        // Al menos 1 minúscula
        if (!password.any { it.isLowerCase() }) return false

        // Al menos 1 número
        if (!password.any { it.isDigit() }) return false

        // Al menos 1 carácter especial
        val caracteresEspeciales = "@#$%!&*"
        return password.any { it in caracteresEspeciales }
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