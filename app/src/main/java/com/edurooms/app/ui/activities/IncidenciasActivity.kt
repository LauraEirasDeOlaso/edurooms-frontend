package com.edurooms.app.ui.activities

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.edurooms.app.R
import com.edurooms.app.data.models.CrearIncidenciaRequest
import com.edurooms.app.data.models.Incidencia
import com.edurooms.app.data.network.RetrofitClient
import com.edurooms.app.data.utils.TokenManager
import com.edurooms.app.ui.adapters.IncidenciasAdapter
import kotlinx.coroutines.launch

class IncidenciasActivity : BaseActivity() {

    private lateinit var incidenciasListView: ListView
    private lateinit var descripcionInput: EditText
    private lateinit var tipoInput: EditText
    private lateinit var reportarButton: Button
    private var incidenciasLista: MutableList<Incidencia> = mutableListOf()

    private var aulaIdRecibido: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_incidencias)

        setupToolbar(title = "Reportar Incidencia", showBackButton = true)
        mostrarIconosToolbar(notificaciones = true, perfil = true)
        configurarIconosToolbar(
            onNotificacionesClick = { Toast.makeText(this, "Notificaciones", Toast.LENGTH_SHORT).show() },
            onPerfilClick = { startActivity(android.content.Intent(this, PerfilActivity::class.java)) }
        )

        // Configurar Bottom Navigation
        setupBottomNavigation()
        seleccionarItemBottomNav(R.id.nav_incidencias)

        aulaIdRecibido = intent.getIntExtra("aula_id", 0)

        // Vincular vistas
        incidenciasListView = findViewById(R.id.incidenciasListView)
        descripcionInput = findViewById(R.id.descripcionInput)
        tipoInput = findViewById(R.id.tipoInput)
        reportarButton = findViewById(R.id.reportarButton)

        // Click listener
        reportarButton.setOnClickListener { reportarIncidencia() }

        // Cargar incidencias
        cargarIncidencias()
    }

    private fun cargarIncidencias() {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.obtenerIncidencias()

                if (response.isSuccessful) {
                    if (response.body() != null) {
                        incidenciasLista = response.body()!!.toMutableList()
                        val adapter = IncidenciasAdapter(this@IncidenciasActivity, incidenciasLista)
                        incidenciasListView.adapter = adapter
                    } else {
                        Toast.makeText(this@IncidenciasActivity, "Sin incidencias", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@IncidenciasActivity, "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@IncidenciasActivity, "Exception: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun reportarIncidencia() {
        val descripcion = descripcionInput.text.toString().trim()
        val tipo = tipoInput.text.toString().trim()

        if (descripcion.isEmpty() || tipo.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        // como en back
        if (descripcion.length < 10) {
            Toast.makeText(this, "La descripción debe tener al menos 10 caracteres", Toast.LENGTH_SHORT).show()
            return
        }

        // AGREGAR ESTA VALIDACIÓN
        val tiposValidos = listOf("electrica", "informatica", "estructural", "limpieza", "otro")
        if (!tiposValidos.contains(tipo.lowercase())) {
            Toast.makeText(this, "Tipo debe ser: electrica, informatica, estructural, limpieza, otro", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                // Obtener token
                val tokenManager = TokenManager(this@IncidenciasActivity)
                val token = tokenManager.obtenerToken()

                if (token == null) {
                    Toast.makeText(this@IncidenciasActivity, "❌ No hay sesión activa", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                // Crear request
                val requestBody = CrearIncidenciaRequest(
                    aula_id = if (aulaIdRecibido != 0) aulaIdRecibido else 1,
                    descripcion = descripcion,
                    tipo = tipo
                )

                // REALMENTE ENVIAR al backend
                val response = RetrofitClient.apiService.crearIncidencia("Bearer $token", requestBody)

                if (response.isSuccessful) {
                    Toast.makeText(this@IncidenciasActivity, "✅ Incidencia reportada", Toast.LENGTH_SHORT).show()

                    // Limpiar campos
                    descripcionInput.setText("")
                    tipoInput.setText("")

                    // Recargar lista
                    cargarIncidencias()
                } else {
                    Toast.makeText(this@IncidenciasActivity, "❌ Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                Toast.makeText(this@IncidenciasActivity, "❌ Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}