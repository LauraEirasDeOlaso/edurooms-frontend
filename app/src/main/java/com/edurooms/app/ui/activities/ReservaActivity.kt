package com.edurooms.app.ui.activities

import android.os.Bundle
import android.widget.Button
import android.widget.CalendarView
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.edurooms.app.R
import com.edurooms.app.data.models.CrearReservaRequest
import com.google.gson.JsonParser
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ReservaActivity : BaseActivity() {

    private lateinit var calendarView: CalendarView
    private lateinit var horaInicioInput: EditText
    private lateinit var horaFinInput: EditText
    private lateinit var confirmarButton: Button
    private lateinit var cancelarButton: Button
    private var aulaId: Int = 0
    private var fechaSeleccionada: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reserva)
        setupBottomNavigation()

        aulaId = intent.getIntExtra("aula_id", 0)

        calendarView = findViewById(R.id.calendarView)
        horaInicioInput = findViewById(R.id.horaInicioInput)
        horaFinInput = findViewById(R.id.horaFinInput)
        confirmarButton = findViewById(R.id.confirmarButton)
        cancelarButton = findViewById(R.id.cancelarButton)

        // Establecer fecha mínima a hoy
        val hoy = Calendar.getInstance()
        calendarView.minDate = hoy.timeInMillis

        // Escuchar cambios de fecha
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val mesFormato = String.format("%02d", month + 1)
            val diaFormato = String.format("%02d", dayOfMonth)
            fechaSeleccionada = "$year-$mesFormato-$diaFormato"
            Toast.makeText(this, "Fecha seleccionada: $fechaSeleccionada", Toast.LENGTH_SHORT).show()
        }

        // Establecer fecha por defecto de hoy
        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val mes = String.format("%02d", cal.get(Calendar.MONTH) + 1)
        val dia = String.format("%02d", cal.get(Calendar.DAY_OF_MONTH))
        fechaSeleccionada = "$year-$mes-$dia"

        confirmarButton.setOnClickListener { crearReserva() }
        cancelarButton.setOnClickListener { finish() }
    }

    private fun crearReserva() {
        val horaInicio = horaInicioInput.text.toString().trim()
        val horaFin = horaFinInput.text.toString().trim()

        if (horaInicio.isEmpty() || horaFin.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        if (!horaInicio.matches(Regex("\\d{2}:\\d{2}")) ||
            !horaFin.matches(Regex("\\d{2}:\\d{2}"))) {
            Toast.makeText(this, "Formato de hora: HH:MM", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                val tokenManager = com.edurooms.app.data.utils.TokenManager(this@ReservaActivity)
                val token = tokenManager.obtenerToken()

                if (token == null) {
                    Toast.makeText(this@ReservaActivity, "❌ No hay sesión", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                val requestBody = CrearReservaRequest(
                    usuario_id = 1,
                    aula_id = aulaId,
                    fecha = fechaSeleccionada,
                    hora_inicio = horaInicio,
                    hora_fin = horaFin
                )

                val response = com.edurooms.app.data.network.RetrofitClient.apiService.crearReserva("Bearer $token", requestBody)

                if (response.isSuccessful) {
                    Toast.makeText(this@ReservaActivity, "✅ Reserva creada", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    try {
                        val errorBody = response.errorBody()?.string()
                        val errorMessage = if (errorBody != null) {
                            val jsonObject = JsonParser.parseString(errorBody).asJsonObject
                            jsonObject.get("mensaje")?.asString ?: "Error desconocido"
                        } else {
                            "Error ${response.code()}"
                        }
                        Toast.makeText(this@ReservaActivity, "❌ $errorMessage", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        Toast.makeText(this@ReservaActivity, "❌ Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(this@ReservaActivity, "❌ Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
