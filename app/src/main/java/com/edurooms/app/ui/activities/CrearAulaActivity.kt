package com.edurooms.app.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.edurooms.app.R
import com.edurooms.app.data.models.CrearAulaRequest
import com.edurooms.app.data.network.RetrofitClient
import com.edurooms.app.data.utils.TokenManager
import kotlinx.coroutines.launch


class CrearAulaActivity : BaseActivity() {

    private lateinit var tokenManager: TokenManager
    private lateinit var nombreInput: EditText
    private lateinit var capacidadInput: EditText
    private lateinit var ubicacionInput: EditText
    private lateinit var codigoQRInput: EditText
    private lateinit var crearButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_aula)

        setupToolbar(title = "Crear Aula", showBackButton = true)
        mostrarIconosToolbar(notificaciones = true, perfil = true)
        configurarIconosToolbar(
            onNotificacionesClick = {
                Toast.makeText(this, "Notificaciones", Toast.LENGTH_SHORT).show()
            },
            onPerfilClick = { startActivity(Intent(this, PerfilActivity::class.java)) }
        )

        setupBottomNavigation()
        tokenManager = TokenManager(this)

        nombreInput = findViewById(R.id.nombreInput)
        capacidadInput = findViewById(R.id.capacidadInput)
        ubicacionInput = findViewById(R.id.ubicacionInput)
        codigoQRInput = findViewById(R.id.codigoQRInput)
        crearButton = findViewById(R.id.crearButton)

        crearButton.setOnClickListener { crearAula() }
    }

    private fun crearAula() {
        val nombre = nombreInput.text.toString()
        val capacidad = capacidadInput.text.toString()
        val ubicacion = ubicacionInput.text.toString()
        val codigoQR = codigoQRInput.text.toString()

        if (nombre.isEmpty() || capacidad.isEmpty()) {
            Toast.makeText(this, "Nombre y capacidad requeridos", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                val token = tokenManager.obtenerToken() ?: return@launch
                val aula = CrearAulaRequest(
                    nombre = nombre,
                    capacidad = capacidad.toInt(),
                    ubicacion = ubicacion.ifEmpty { null },
                    codigo_qr = codigoQR.ifEmpty { null }
                )

                val response = RetrofitClient.apiService.crearAula("Bearer $token", aula)

                if (response.isSuccessful) {
                    Toast.makeText(this@CrearAulaActivity, "✅ Aula creada", Toast.LENGTH_SHORT)
                        .show()
                    startActivity(
                        Intent(
                            this@CrearAulaActivity,
                            GestionarAulasActivity::class.java
                        )
                    )
                    finish()
                } else {
                    // ← NUEVO: Parsear error del backend
                    val errorBody = response.errorBody()?.string() ?: ""
                    val errorMsg = try {
                        val errorJson = org.json.JSONObject(errorBody)
                        errorJson.optString("mensaje", "Error desconocido")
                    } catch (_: Exception) {
                        "Error ${response.code()}"
                    }
                    Toast.makeText(this@CrearAulaActivity, "❌ $errorMsg", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@CrearAulaActivity, "Error: ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}