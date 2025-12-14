package com.edurooms.app.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.edurooms.app.R
import com.edurooms.app.data.network.RetrofitClient
import kotlinx.coroutines.launch

class DetalleIncidenciaActivity : BaseActivity() {

    private lateinit var aulaText: TextView
    private lateinit var usuarioText: TextView
    private lateinit var descripcionText: TextView
    private lateinit var tipoText: TextView
    private lateinit var estadoText: TextView

    private var incidenciaId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_incidencia)

        setupToolbar(title = "Detalle Incidencia", showBackButton = true)
        mostrarIconosToolbar(notificaciones = true, perfil = true)
        configurarIconosToolbar(
            onNotificacionesClick = { Toast.makeText(this, "Notificaciones", Toast.LENGTH_SHORT).show() },
            onPerfilClick = { startActivity(Intent(this, PerfilActivity::class.java)) }
        )

        setupBottomNavigation()

        incidenciaId = intent.getIntExtra("incidencia_id", 0)

        aulaText = findViewById(R.id.aulaText)
        usuarioText = findViewById(R.id.usuarioText)
        descripcionText = findViewById(R.id.descripcionText)
        tipoText = findViewById(R.id.tipoText)
        estadoText = findViewById(R.id.estadoText)

        cargarDetalleIncidencia()
    }

    private fun cargarDetalleIncidencia() {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.obtenerIncidenciaPorId(incidenciaId)

                if (response.isSuccessful && response.body() != null) {
                    val incidencia = response.body()!!

                    aulaText.text = incidencia.aula_nombre ?: "Desconocida"
                    usuarioText.text = incidencia.usuario_nombre ?: "Desconocido"
                    descripcionText.text = incidencia.descripcion
                    tipoText.text = incidencia.tipo
                    estadoText.text = incidencia.estado

                    // Colorear estado
                    when (incidencia.estado) {
                        "pendiente" -> estadoText.setTextColor(getColor(R.color.warning))
                        "en_revision" -> estadoText.setTextColor(getColor(R.color.primary))
                        "resuelta" -> estadoText.setTextColor(getColor(R.color.success))
                    }
                } else {
                    Toast.makeText(this@DetalleIncidenciaActivity, "Error al cargar incidencia", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@DetalleIncidenciaActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}