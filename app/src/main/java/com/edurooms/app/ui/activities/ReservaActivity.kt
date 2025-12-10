package com.edurooms.app.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CalendarView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.edurooms.app.R
import com.edurooms.app.data.models.CrearReservaRequest
import com.edurooms.app.data.network.RetrofitClient
import com.edurooms.app.data.utils.TokenManager
import com.google.gson.JsonParser
import kotlinx.coroutines.launch
import java.util.*
import android.widget.ArrayAdapter


class ReservaActivity : BaseActivity() {

    private lateinit var calendarView: CalendarView

    private lateinit var confirmarButton: Button
    private lateinit var cancelarButton: Button
    private var aulaId: Int = 0
    private var fechaSeleccionada: String = ""

    private lateinit var spinnerHorarios: Spinner

    private lateinit var horariosDisponiblesText: TextView

    private var horariosLibres: List<Pair<String, String>> = emptyList()

    private var horaSeleccionada: String = ""

    private lateinit var actualizarButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reserva)
        setupBottomNavigation()

        aulaId = intent.getIntExtra("aula_id", 0)

        calendarView = findViewById(R.id.calendarView)
        confirmarButton = findViewById(R.id.confirmarButton)
        cancelarButton = findViewById(R.id.cancelarButton)
        spinnerHorarios = findViewById(R.id.spinnerHorarios)
        horariosDisponiblesText = findViewById(R.id.horariosDisponiblesText)
        actualizarButton = findViewById(R.id.actualizarButton)

        // Establecer fecha mínima a hoy
        val hoy = Calendar.getInstance()
        calendarView.minDate = hoy.timeInMillis

        // Fecha por defecto: hoy
        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val mes = String.format("%02d", cal.get(Calendar.MONTH) + 1)
        val dia = String.format("%02d", cal.get(Calendar.DAY_OF_MONTH))
        fechaSeleccionada = "$year-$mes-$dia"

        // Cargar horarios para hoy
        cargarHorariosDisponibles(fechaSeleccionada)

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val mesFormato = String.format("%02d", month + 1)
            val diaFormato = String.format("%02d", dayOfMonth)
            fechaSeleccionada = "$year-$mesFormato-$diaFormato"
            //Toast.makeText(this, "Fecha seleccionada: $fechaSeleccionada", Toast.LENGTH_SHORT).show()

            // Cargar horarios para la nueva fecha
            cargarHorariosDisponibles(fechaSeleccionada)
        }

        // Listener para spinner
        spinnerHorarios.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>, view: android.view.View, position: Int, id: Long) {
                if (position > 0 && position <= horariosLibres.size) {
                    val (inicio, fin) = horariosLibres[position - 1]
                    horaSeleccionada = "$inicio-$fin"
                }
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>) {}
        }

        // Click listeners
        confirmarButton.setOnClickListener { crearReserva() }
        cancelarButton.setOnClickListener { finish() }
        actualizarButton.setOnClickListener { recargarHorarios() }

    }

    private fun cargarHorariosDisponibles(fecha: String) {
        lifecycleScope.launch {
            try {
                // Mostrar loading
                horariosDisponiblesText.text = "Cargando horarios..."
                spinnerHorarios.isEnabled = false

                val response = RetrofitClient.apiService.obtenerHorariosDisponibles(aulaId, fecha)

                if (response.isSuccessful && response.body() != null) {
                    val disponibilidad = response.body()!!.disponibilidad
                    horariosLibres = disponibilidad.horariosLibres.map {
                        Pair(it.hora_inicio, it.hora_fin)
                    }

                    if (horariosLibres.isEmpty()) {
                        horariosDisponiblesText.text = "❌ No hay horarios disponibles para esta fecha"
                        spinnerHorarios.isEnabled = false
                    } else {
                        horariosDisponiblesText.text = "✅ ${horariosLibres.size} horarios disponibles"

                        // Crear lista para el spinner
                        val items = mutableListOf("Selecciona un horario...")
                        items.addAll(horariosLibres.map { (inicio, fin) -> "$inicio - $fin" })

                        val adapter = ArrayAdapter(
                            this@ReservaActivity,
                            android.R.layout.simple_spinner_item,
                            items
                        )
                        adapter.setDropDownViewResource(R.layout.custom_spinner_item)

                        spinnerHorarios.adapter = adapter
                        spinnerHorarios.isEnabled = true
                    }
                } else {
                    // Error del backend (fecha pasada, sábado, festivo, etc)
                    val errorBody = response.errorBody()?.string()
                    val errorMsg = if (errorBody != null) {
                        try {
                            val jsonObject = JsonParser.parseString(errorBody).asJsonObject
                            jsonObject.get("mensaje")?.asString ?: "Error desconocido"
                        } catch (e: Exception) {
                            "Error: ${response.code()}"
                        }
                    } else {
                        "Error: ${response.code()}"
                    }

                    horariosDisponiblesText.text = "❌ $errorMsg"
                    spinnerHorarios.isEnabled = false
                    horariosLibres = emptyList()
                }
            } catch (e: Exception) {
                horariosDisponiblesText.text = "❌ Error: ${e.message}"
                spinnerHorarios.isEnabled = false
                android.util.Log.e("RESERVA", "Error cargando horarios", e)
            }
        }
    }

    private fun crearReserva() {
        if (horaSeleccionada.isEmpty()) {
            Toast.makeText(this, "❌ Selecciona un horario", Toast.LENGTH_SHORT).show()
            return
        }

        val (horaInicio, horaFin) = horaSeleccionada.split("-").let {
            Pair(it[0].trim(), it[1].trim())
        }

        lifecycleScope.launch {
            try {
                val tokenManager = TokenManager(this@ReservaActivity)
                val token = tokenManager.obtenerToken()

                if (token == null) {
                    Toast.makeText(this@ReservaActivity, "❌ No hay sesión", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                // Obtener usuario_id del perfil
                val perfilResponse = RetrofitClient.apiService.obtenerPerfil("Bearer $token")
                if (!perfilResponse.isSuccessful || perfilResponse.body() == null) {
                    Toast.makeText(this@ReservaActivity, "❌ Error al obtener usuario", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                val usuarioId = perfilResponse.body()?.usuario?.id ?: 0

                if (usuarioId == 0) {
                    Toast.makeText(this@ReservaActivity, "❌ No se pudo obtener usuario", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                val requestBody = CrearReservaRequest(
                    usuario_id = usuarioId,
                    aula_id = aulaId,
                    fecha = fechaSeleccionada,
                    hora_inicio = horaInicio,
                    hora_fin = horaFin
                )

                val response = RetrofitClient.apiService.crearReserva("Bearer $token", requestBody)

                if (response.isSuccessful) {
                    Toast.makeText(this@ReservaActivity, "✅ Reserva creada correctamente", Toast.LENGTH_SHORT).show()
                    // Ir a Mis Reservas en lugar de volver atrás
                    startActivity(Intent(this@ReservaActivity, MisReservasActivity::class.java))
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

    private fun recargarHorarios() {
        if (fechaSeleccionada.isEmpty()) {
            Toast.makeText(this, "Selecciona una fecha primero", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                actualizarButton.isEnabled = false
                val response = RetrofitClient.apiService.obtenerHorariosDisponibles(aulaId, fechaSeleccionada)

                if (response.isSuccessful && response.body() != null) {
                    val disponibilidad = response.body()!!.disponibilidad
                    horariosLibres = disponibilidad.horariosLibres.map {
                        Pair(it.hora_inicio, it.hora_fin)
                    }

                    // Recrear el spinner
                    val items = mutableListOf("Selecciona un horario...")
                    items.addAll(horariosLibres.map { (inicio, fin) -> "$inicio - $fin" })

                    val adapter = ArrayAdapter(
                        this@ReservaActivity,
                        android.R.layout.simple_spinner_item,
                        items
                    )
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerHorarios.adapter = adapter

                    Toast.makeText(this@ReservaActivity, "✅ Horarios actualizados", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@ReservaActivity, "Error al actualizar", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@ReservaActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                actualizarButton.isEnabled = true
            }
        }
    }
}


