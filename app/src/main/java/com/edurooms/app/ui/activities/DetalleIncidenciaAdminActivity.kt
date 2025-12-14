package com.edurooms.app.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.edurooms.app.R
import com.edurooms.app.data.models.ActualizarIncidenciaRequest
import com.edurooms.app.data.network.RetrofitClient
import com.edurooms.app.data.utils.TokenManager
import kotlinx.coroutines.launch

class DetalleIncidenciaAdminActivity : BaseActivity() {

    private lateinit var tokenManager: TokenManager
    private lateinit var aulaText: TextView
    private lateinit var usuarioText: TextView
    private lateinit var descripcionText: TextView
    private lateinit var tipoText: TextView
    private lateinit var estadoSpinner: Spinner

    private var incidenciaId: Int = 0
    private var estadoActual: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_incidencia_admin)

        setupToolbar(title = "Detalle Incidencia", showBackButton = true)
        mostrarIconosToolbar(notificaciones = true, perfil = true)
        configurarIconosToolbar(
            onNotificacionesClick = { Toast.makeText(this, "Notificaciones", Toast.LENGTH_SHORT).show() },
            onPerfilClick = { startActivity(Intent(this, PerfilActivity::class.java)) }
        )

        setupBottomNavigation()
        tokenManager = TokenManager(this)

        incidenciaId = intent.getIntExtra("incidencia_id", 0)

        aulaText = findViewById(R.id.aulaText)
        usuarioText = findViewById(R.id.usuarioText)
        descripcionText = findViewById(R.id.descripcionText)
        tipoText = findViewById(R.id.tipoText)
        estadoSpinner = findViewById(R.id.estadoSpinner)

        cargarDetalleIncidencia()
    }

    private fun cargarDetalleIncidencia() {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.obtenerIncidenciaPorId(incidenciaId)

                if (response.isSuccessful && response.body() != null) {
                    val incidencia = response.body()!!
                    estadoActual = incidencia.estado

                    aulaText.text = incidencia.aula_nombre ?: "Desconocida"
                    usuarioText.text = incidencia.usuario_nombre ?: "Desconocido"
                    descripcionText.text = incidencia.descripcion
                    tipoText.text = incidencia.tipo

                    configurarSpinner()
                } else {
                    Toast.makeText(this@DetalleIncidenciaAdminActivity, "Error al cargar incidencia", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@DetalleIncidenciaAdminActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun configurarSpinner() {
        val estados = arrayOf("pendiente", "en_revision", "resuelta")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, estados)
        adapter.setDropDownViewResource(R.layout.custom_spinner_item)
        estadoSpinner.adapter = adapter

        val index = estados.indexOf(estadoActual)
        estadoSpinner.setSelection(index)

        estadoSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val nuevoEstado = estados[position]
                if (nuevoEstado != estadoActual) {
                    actualizarEstado(nuevoEstado)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun actualizarEstado(nuevoEstado: String) {
        android.util.Log.d("UPDATE_ESTADO", "Entrando a actualizarEstado con: $nuevoEstado")
        lifecycleScope.launch {
            android.util.Log.d("UPDATE_ESTADO", "Dentro del launch")
            try {
                val token = tokenManager.obtenerToken() ?: return@launch
                android.util.Log.d("UPDATE_ESTADO", "Token obtenido: $token")

                val request = ActualizarIncidenciaRequest(nuevoEstado)
                android.util.Log.d("UPDATE_ESTADO", "Request creado: $request")

                android.util.Log.d("UPDATE_ESTADO", "Incidencia ID: $incidenciaId")

                val response = RetrofitClient.apiService.actualizarIncidencia("Bearer $token", incidenciaId, request)
                android.util.Log.d("UPDATE_ESTADO", "Response code: ${response.code()}")

                if (response.isSuccessful) {
                    Toast.makeText(this@DetalleIncidenciaAdminActivity, "✅ Estado actualizado", Toast.LENGTH_SHORT).show()
                    estadoActual = nuevoEstado
                    finish()
                } else {
                    Toast.makeText(this@DetalleIncidenciaAdminActivity, "❌ Error al actualizar", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                android.util.Log.e("UPDATE_ESTADO", "Exception: ${e.message}")
                Toast.makeText(this@DetalleIncidenciaAdminActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}