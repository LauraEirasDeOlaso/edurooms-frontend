package com.edurooms.app.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Spinner
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.edurooms.app.R
import com.edurooms.app.data.models.Reserva
import com.edurooms.app.data.network.RetrofitClient
import com.edurooms.app.data.utils.TokenManager
import com.edurooms.app.ui.adapters.ReservasAdapter
import kotlinx.coroutines.launch
import android.widget.ArrayAdapter


class GestionarReservasActivity : BaseActivity() {

    private lateinit var tokenManager: TokenManager
    private lateinit var reservasRecyclerView: RecyclerView
    private lateinit var estadoSpinner: Spinner
    private lateinit var aulaSpinner: Spinner
    private lateinit var usuarioSpinner: Spinner
    private var todasLasReservas: List<Reserva> = emptyList()
    private var aulasSet: Set<String> = emptySet()
    private var usuariosSet: Set<String> = emptySet()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gestionar_reservas)

        setupToolbar(title = "Gestionar Reservas", showBackButton = true)
        mostrarIconosToolbar(notificaciones = true, perfil = true)
        configurarIconosToolbar(
            onNotificacionesClick = {
                Toast.makeText(this, "Notificaciones", Toast.LENGTH_SHORT).show()
            },
            onPerfilClick = { startActivity(Intent(this, PerfilActivity::class.java)) }
        )

        setupBottomNavigation()
        tokenManager = TokenManager(this)

        reservasRecyclerView = findViewById(R.id.reservasRecyclerView)
        estadoSpinner = findViewById(R.id.estadoSpinner)
        aulaSpinner = findViewById(R.id.aulaSpinner)
        usuarioSpinner = findViewById(R.id.usuarioSpinner)
        reservasRecyclerView.layoutManager = LinearLayoutManager(this)

        cargarTodasReservas()
    }

    private fun cargarTodasReservas() {
        lifecycleScope.launch {
            try {
                val token = tokenManager.obtenerToken() ?: return@launch
                val response = RetrofitClient.apiService.obtenerTodasReservas("Bearer $token")

                if (response.isSuccessful && response.body() != null) {
                    todasLasReservas = response.body()!!
                    if (todasLasReservas.isNotEmpty()) {
                        configurarFiltros()
                        mostrarReservas(todasLasReservas)
                    } else {
                        Toast.makeText(
                            this@GestionarReservasActivity,
                            "No hay reservas",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@GestionarReservasActivity,
                    "Error: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun configurarFiltros() {
        // Extraer valores únicos
        val estados = resources.getStringArray(R.array.estados_reservas).toList()
        aulasSet = todasLasReservas.map { it.aula_nombre }.toSet()
        usuariosSet = todasLasReservas.map { it.usuario_nombre ?: "Desconocido" }.toSet()

        val aulas = listOf("Todas las aulas") + aulasSet.toList()
        val usuarios = listOf("Todos los usuarios") + usuariosSet.toList()

        // Configurar adaptadores
        val estadoAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, estados)
        val aulaAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, aulas)
        val usuarioAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, usuarios)
        estadoAdapter.setDropDownViewResource(R.layout.custom_spinner_item)
        aulaAdapter.setDropDownViewResource(R.layout.custom_spinner_item)
        usuarioAdapter.setDropDownViewResource(R.layout.custom_spinner_item)

        estadoSpinner.adapter = estadoAdapter
        aulaSpinner.adapter = aulaAdapter
        usuarioSpinner.adapter = usuarioAdapter

        // Listeners para filtrar
        estadoSpinner.onItemSelectedListener =
            object : android.widget.AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: android.widget.AdapterView<*>,
                    view: android.view.View?,
                    position: Int,
                    id: Long
                ) {
                    aplicarFiltros()
                }

                override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
            }

        aulaSpinner.onItemSelectedListener =
            object : android.widget.AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: android.widget.AdapterView<*>,
                    view: android.view.View?,
                    position: Int,
                    id: Long
                ) {
                    aplicarFiltros()
                }

                override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
            }

        usuarioSpinner.onItemSelectedListener =
            object : android.widget.AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: android.widget.AdapterView<*>,
                    view: android.view.View?,
                    position: Int,
                    id: Long
                ) {
                    aplicarFiltros()
                }

                override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
            }
    }

    private fun aplicarFiltros() {
        val estadoSeleccionado = estadoSpinner.selectedItem.toString()
        val aulaSeleccionada = aulaSpinner.selectedItem.toString()
        val usuarioSeleccionado = usuarioSpinner.selectedItem.toString()

        val filtradas = todasLasReservas.filter { reserva ->
            val cumpleEstado = estadoSeleccionado == "Todas" || reserva.estado == estadoSeleccionado
            val cumpleAula =
                aulaSeleccionada == "Todas las aulas" || reserva.aula_nombre == aulaSeleccionada
            val cumpleUsuario =
                usuarioSeleccionado == "Todos los usuarios" || reserva.usuario_nombre == usuarioSeleccionado

            cumpleEstado && cumpleAula && cumpleUsuario
        }

        mostrarReservas(filtradas)
    }

    private fun mostrarReservas(reservas: List<Reserva>) {
        val adapter = ReservasAdapter(reservas) { reserva ->
            val intent = Intent(this@GestionarReservasActivity, DetalleReservaActivity::class.java)
            intent.putExtra("reserva_id", reserva.id)
            intent.putExtra("es_desde_gestionar", true)
            startActivity(intent)
        }
        reservasRecyclerView.adapter = adapter
    }

    override fun onSupportNavigateUp(): Boolean {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
        return true
    }

    override fun onResume() {
        super.onResume()
        cargarTodasReservas()
    }
}