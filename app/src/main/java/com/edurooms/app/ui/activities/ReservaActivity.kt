package com.edurooms.app.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CalendarView
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
import androidx.recyclerview.widget.RecyclerView
import com.edurooms.app.ui.adapters.HorarioItem
import com.edurooms.app.ui.adapters.HorariosAdapter
import androidx.recyclerview.widget.LinearLayoutManager



class ReservaActivity : BaseActivity() {

    private lateinit var calendarView: CalendarView
    private lateinit var confirmarButton: Button
    private lateinit var cancelarButton: Button
    private lateinit var horariosRecyclerView: RecyclerView
    private lateinit var horariosDisponiblesText: TextView
    private lateinit var actualizarButton: Button

    private lateinit var horariosAdapter: HorariosAdapter

    private var aulaId: Int = 0
    private var fechaSeleccionada: String = ""
    private var horarioSeleccionado: HorarioItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reserva)
        setupBottomNavigation()

        aulaId = intent.getIntExtra("aula_id", 0)

        calendarView = findViewById(R.id.calendarView)
        confirmarButton = findViewById(R.id.confirmarButton)
        cancelarButton = findViewById(R.id.cancelarButton)
        horariosRecyclerView = findViewById(R.id.horariosRecyclerView)
        horariosDisponiblesText = findViewById(R.id.horariosDisponiblesText)
        actualizarButton = findViewById(R.id.actualizarButton)

        // Desactivar confirmar al inicio
        confirmarButton.isEnabled = false

        horariosRecyclerView.layoutManager = LinearLayoutManager(this)
        horariosRecyclerView.setHasFixedSize(true)

        // Establecer fecha mínima a hoy
        val hoy = Calendar.getInstance()
        calendarView.minDate = hoy.timeInMillis

        // Fecha por defecto: hoy
        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val mes = String.format(Locale.US,"%02d", cal.get(Calendar.MONTH) + 1)
        val dia = String.format(Locale.US, "%02d", cal.get(Calendar.DAY_OF_MONTH))
        fechaSeleccionada = "$year-$mes-$dia"

        // Cargar horarios para hoy
        cargarHorariosDisponibles(fechaSeleccionada)

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val mes = String.format(Locale.US, "%02d",month + 1)
            val dia = String.format(Locale.US, "%02d",dayOfMonth)
            fechaSeleccionada = "$year-$mes-$dia"
            //Toast.makeText(this, "Fecha seleccionada: $fechaSeleccionada", Toast.LENGTH_SHORT).show()

            // Cargar horarios para la nueva fecha
            cargarHorariosDisponibles(fechaSeleccionada)
        }


        // Click listeners
        confirmarButton.setOnClickListener { crearReserva() }
        cancelarButton.setOnClickListener { finish() }
        actualizarButton.setOnClickListener { cargarHorariosDisponibles(fechaSeleccionada) }

    }
    override fun onResume() {
        super.onResume()
        cargarHorariosDisponibles(fechaSeleccionada)
    }

    private fun cargarHorariosDisponibles(fecha: String) {
        lifecycleScope.launch {
            try {
                horariosDisponiblesText.text = getString(R.string.horarios_cargando)
                horariosRecyclerView.adapter = null

                // limpiar selección al recargar
                horarioSeleccionado = null
                confirmarButton.isEnabled = false

                val response = RetrofitClient.apiService.obtenerHorariosDisponibles(aulaId, fecha)

                if (response.isSuccessful && response.body() != null) {
                    val disponibilidad = response.body()!!.disponibilidad

                    // Crear lista con TODOS los horarios (libres + ocupados)
                    val todosHorarios = mutableListOf<HorarioItem>()

                    // Agregar horarios libres
                    todosHorarios.addAll(disponibilidad.horariosLibres.map { horario ->
                        HorarioItem(horario.hora_inicio, horario.hora_fin, libre = true)
                    })

                    // Agregar horarios ocupados
                    todosHorarios.addAll(disponibilidad.horariosOcupados.map { horario ->
                        HorarioItem(horario.hora_inicio, horario.hora_fin, libre = false)
                    })

                    // Ordenar por hora_inicio
                    todosHorarios.sortBy { it.hora_inicio }

                    if (todosHorarios.isEmpty()) {
                        horariosDisponiblesText.text = getString(R.string.horarios_no_disponibles)
                    } else {
                        val libres = disponibilidad.horariosLibres.size
                        horariosDisponiblesText.text = getString(R.string.horarios_disponibles, libres)

                        // Crear adapter
                        horariosAdapter = HorariosAdapter(todosHorarios) { horario ->
                            horarioSeleccionado = horario

                            // activar confirmar
                            confirmarButton.isEnabled = true
                        }
                        horariosRecyclerView.adapter = horariosAdapter
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMsg = if (errorBody != null) {
                        try {
                            val jsonObject = JsonParser.parseString(errorBody).asJsonObject
                            jsonObject.get("mensaje")?.asString ?: "Error desconocido"
                        } catch (_: Exception) {
                            "Error: ${response.code()}"
                        }
                    } else {
                        "Error: ${response.code()}"
                    }
                    horariosDisponiblesText.text = getString(R.string.error_mensaje_formato, errorMsg)
                }
            } catch (e: Exception) {
                horariosDisponiblesText.text = getString(R.string.error_excepcion_formato, e.message ?: "Desconocido")
                android.util.Log.e("RESERVA", "Error cargando horarios", e)
            }
        }
    }

    private fun crearReserva() {
        if (horarioSeleccionado == null) {
            Toast.makeText(this, "❌ Selecciona un horario disponible", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                val tokenManager = TokenManager(this@ReservaActivity)
                val token = tokenManager.obtenerToken()

                if (token == null) {
                    Toast.makeText(this@ReservaActivity, "❌ No hay sesión", Toast.LENGTH_SHORT).show()
                    return@launch
                }

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
                    hora_inicio = horarioSeleccionado!!.hora_inicio,
                    hora_fin = horarioSeleccionado!!.hora_fin
                )

                val response = RetrofitClient.apiService.crearReserva("Bearer $token", requestBody)

                if (response.isSuccessful) {
                    Toast.makeText(this@ReservaActivity, "✅ Reserva creada correctamente", Toast.LENGTH_SHORT).show()
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
                    } catch (_: Exception) {
                        Toast.makeText(this@ReservaActivity, "❌ Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(this@ReservaActivity, "❌ Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

}


