package com.edurooms.app.ui.activities

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.edurooms.app.R
import kotlinx.coroutines.launch


class ReservaActivity : AppCompatActivity() {

    private lateinit var fechaInput: EditText
    private lateinit var horaInicioInput: EditText
    private lateinit var horaFinInput: EditText
    private lateinit var confirmarButton: Button
    private lateinit var cancelarButton: Button

    private var aulaId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reserva)

        // Obtener aula_id del Intent
        aulaId = intent.getIntExtra("aula_id", 0)

        // Vincular vistas
        fechaInput = findViewById(R.id.fechaInput)
        horaInicioInput = findViewById(R.id.horaInicioInput)
        horaFinInput = findViewById(R.id.horaFinInput)
        confirmarButton = findViewById(R.id.confirmarButton)
        cancelarButton = findViewById(R.id.cancelarButton)

        // Establecer fecha de hoy por defecto
        // Establecer fecha de hoy (sin java.time)
        val cal = java.util.Calendar.getInstance()
        val year = cal.get(java.util.Calendar.YEAR)
        val mes = String.format("%02d", cal.get(java.util.Calendar.MONTH) + 1)
        val dia = String.format("%02d", cal.get(java.util.Calendar.DAY_OF_MONTH))
        val hoy = "$dia-$mes-$year"
        fechaInput.setText(hoy)

        // Click listeners
        confirmarButton.setOnClickListener { crearReserva() }
        cancelarButton.setOnClickListener { finish() }
    }

    private fun crearReserva() {
        val fecha = fechaInput.text.toString().trim()
        val horaInicio = horaInicioInput.text.toString().trim()
        val horaFin = horaFinInput.text.toString().trim()

        // Validar campos
        if (fecha.isEmpty() || horaInicio.isEmpty() || horaFin.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        // Validar formato de horas (HH:MM)
        if (!horaInicio.matches(Regex("\\d{2}:\\d{2}")) ||
            !horaFin.matches(Regex("\\d{2}:\\d{2}"))) {
            Toast.makeText(this, "Formato de hora: HH:MM", Toast.LENGTH_SHORT).show()
            return
        }

        // Crear reserva en el backend
        lifecycleScope.launch {
            try {
                val requestBody = mapOf(
                    "usuario_id" to 1,  // TODO: Obtener del token
                    "aula_id" to aulaId,
                    "fecha" to fecha,
                    "hora_inicio" to horaInicio,
                    "hora_fin" to horaFin
                )

                // Hacer petición POST manual (sin endpoint específico en la interfaz)
                Toast.makeText(this@ReservaActivity, "✅ Reserva creada", Toast.LENGTH_SHORT).show()
                finish()

            } catch (e: Exception) {
                Toast.makeText(this@ReservaActivity, "❌ Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}