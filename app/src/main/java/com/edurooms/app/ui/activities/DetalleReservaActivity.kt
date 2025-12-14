package com.edurooms.app.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.edurooms.app.R
import com.edurooms.app.data.network.RetrofitClient
import com.edurooms.app.data.utils.TokenManager
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import android.view.View

class DetalleReservaActivity : BaseActivity() {

    private lateinit var aulaNombreText: TextView
    private lateinit var fechaText: TextView
    private lateinit var horaText: TextView
    private lateinit var estadoText: TextView
    private lateinit var cancelarButton: Button
    private lateinit var editarButton: Button
    private lateinit var traspasoButton: Button
    private lateinit var reactivarButton: Button
    private var reservaId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_reserva)

        setupToolbar(title = "Detalle Reserva", showBackButton = true)
        mostrarIconosToolbar(perfil = true, notificaciones = true)
        configurarIconosToolbar(
            onNotificacionesClick = { Toast.makeText(this, "Notificaciones", Toast.LENGTH_SHORT).show() },
            onPerfilClick = { startActivity(Intent(this, PerfilActivity::class.java)) }
        )

        setupBottomNavigation()

        reservaId = intent.getIntExtra("reserva_id", 0)

        aulaNombreText = findViewById(R.id.aulaNombreText)
        fechaText = findViewById(R.id.fechaText)
        horaText = findViewById(R.id.horaText)
        estadoText = findViewById(R.id.estadoText)
        cancelarButton = findViewById(R.id.cancelarButton)
        editarButton = findViewById(R.id.editarButton)
        reactivarButton = findViewById(R.id.reactivarButton)
        traspasoButton = findViewById(R.id.traspasoButton)

        cargarDetalleReserva()

        cancelarButton.setOnClickListener { cancelarReserva() }
        editarButton.setOnClickListener { Toast.makeText(this, "Editar - Próximamente", Toast.LENGTH_SHORT).show() }
        reactivarButton.setOnClickListener { reactivarReserva() }
        traspasoButton.setOnClickListener { Toast.makeText(this, "Traspasar - Próximamente", Toast.LENGTH_SHORT).show() }
    }

    private fun cargarDetalleReserva() {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.obtenerReservaPorId(reservaId)

                if (response.isSuccessful && response.body() != null) {
                    val reserva = response.body()!!

                    aulaNombreText.text = reserva.aula_nombre

                    val esLocale = Locale.Builder()
                        .setLanguage("es")
                        .setRegion("ES")
                        .build()

                    val inputFormat = SimpleDateFormat("yyyy-MM-dd", esLocale)
                    val outputFormat = SimpleDateFormat("dd/MM/yyyy", esLocale)
                    val fechaParsed = inputFormat.parse(reserva.fecha.take(10))
                    val fechaFormateada = if (fechaParsed != null) {
                        outputFormat.format(fechaParsed)  // ✅ Ahora es Date (no-nullable)
                    } else {
                        "Fecha inválida"
                    }
                    fechaText.text = getString(R.string.fecha_formato, fechaFormateada)
                    horaText.text = getString(R.string.horario_formato, reserva.hora_inicio, reserva.hora_fin)
                    estadoText.text = getString(R.string.estado_formato, reserva.estado)
                    //Color según estado
                    when (reserva.estado) {
                        "confirmada" -> estadoText.setTextColor(getColor(R.color.success))
                        "cancelada" -> estadoText.setTextColor(getColor(R.color.error))
                        else -> estadoText.setTextColor(getColor(R.color.warning))
                    }
                    // NUEVO: Mostrar botones según rol y estado
                    val tokenManager = TokenManager(this@DetalleReservaActivity)
                    val esAdmin = tokenManager.obtenerRol() == "administrador"
                    val estaCancelada = reserva.estado == "cancelada"

                    if (esAdmin) {
                        // Admin ve todos los botones
                        if (estaCancelada) {
                            cancelarButton.visibility = View.GONE
                            reactivarButton.visibility = View.VISIBLE
                        } else {
                            cancelarButton.visibility = View.VISIBLE
                            reactivarButton.visibility = View.GONE
                        }
                        editarButton.visibility = View.VISIBLE
                        traspasoButton.visibility = View.VISIBLE
                    } else {
                        // Profesor solo ve cancelar
                        if (estaCancelada) {
                            cancelarButton.visibility = View.GONE
                        } else {
                            cancelarButton.visibility = View.VISIBLE
                        }
                        reactivarButton.visibility = View.GONE
                        editarButton.visibility = View.GONE
                        traspasoButton.visibility = View.GONE
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(this@DetalleReservaActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun cancelarReserva() {
        lifecycleScope.launch {
            try {
                val tokenManager = TokenManager(this@DetalleReservaActivity)
                val token = tokenManager.obtenerToken() ?: return@launch

                val response = RetrofitClient.apiService.cancelarReserva("Bearer $token", reservaId)

                if (response.isSuccessful) {
                    Toast.makeText(this@DetalleReservaActivity, "✅ Reserva cancelada", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@DetalleReservaActivity, MisReservasActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this@DetalleReservaActivity, "Error al cancelar", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@DetalleReservaActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun reactivarReserva() {
        Toast.makeText(this, "Reactivar - Próximamente", Toast.LENGTH_SHORT).show()
    }

    override fun onSupportNavigateUp(): Boolean {
        startActivity(Intent(this, MisReservasActivity::class.java))
        finish()
        return true
    }
}
