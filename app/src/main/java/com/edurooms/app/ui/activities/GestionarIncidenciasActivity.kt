package com.edurooms.app.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Spinner
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.edurooms.app.R
import com.edurooms.app.data.models.Incidencia
import com.edurooms.app.data.network.RetrofitClient
import com.edurooms.app.data.utils.TokenManager
import com.edurooms.app.ui.adapters.IncidenciasRecyclerAdapter
import kotlinx.coroutines.launch
import android.widget.ArrayAdapter

class GestionarIncidenciasActivity : BaseActivity() {

    private lateinit var tokenManager: TokenManager
    private lateinit var incidenciasRecyclerView: RecyclerView
    private lateinit var estadoSpinner: Spinner
    private lateinit var aulaSpinner: Spinner
    private lateinit var usuarioSpinner: Spinner
    private var todasLasIncidencias: List<Incidencia> = emptyList()
    private var aulasSet: Set<String> = emptySet()
    private var usuariosSet: Set<String> = emptySet()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gestionar_incidencias)

        setupToolbar(title = "Gestionar Incidencias", showBackButton = true)
        mostrarIconosToolbar(notificaciones = true, perfil = true)
        configurarIconosToolbar(
            onNotificacionesClick = { Toast.makeText(this, "Notificaciones", Toast.LENGTH_SHORT).show() },
            onPerfilClick = { startActivity(Intent(this, PerfilActivity::class.java)) }
        )

        setupBottomNavigation()
        tokenManager = TokenManager(this)

        incidenciasRecyclerView = findViewById(R.id.incidenciasRecyclerView)
        estadoSpinner = findViewById(R.id.estadoSpinner)
        aulaSpinner = findViewById(R.id.aulaSpinner)
        usuarioSpinner = findViewById(R.id.usuarioSpinner)
        incidenciasRecyclerView.layoutManager = LinearLayoutManager(this)

        cargarTodasIncidencias()
    }

    private fun cargarTodasIncidencias() {
        lifecycleScope.launch {
            try {
                val token = tokenManager.obtenerToken() ?: return@launch
                val response = RetrofitClient.apiService.obtenerIncidencias()

                if (response.isSuccessful && response.body() != null) {
                    todasLasIncidencias = response.body()!!
                    if (todasLasIncidencias.isNotEmpty()) {
                        configurarFiltros()
                        mostrarIncidencias(todasLasIncidencias)
                    } else {
                        Toast.makeText(this@GestionarIncidenciasActivity, "No hay incidencias", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(this@GestionarIncidenciasActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun configurarFiltros() {
        // Extraer valores únicos
        val estados = listOf("Todas") + todasLasIncidencias.map { it.estado }.distinct()
        aulasSet = todasLasIncidencias.map { it.aula_nombre ?: "Desconocida" }.toSet()
        usuariosSet = todasLasIncidencias.map { it.usuario_nombre ?: "Desconocido" }.toSet()

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
        estadoSpinner.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                aplicarFiltros()
            }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        }

        aulaSpinner.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                aplicarFiltros()
            }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        }

        usuarioSpinner.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                aplicarFiltros()
            }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        }
    }

    private fun aplicarFiltros() {
        val estadoSeleccionado = estadoSpinner.selectedItem.toString()
        val aulaSeleccionada = aulaSpinner.selectedItem.toString()
        val usuarioSeleccionado = usuarioSpinner.selectedItem.toString()

        val filtradas = todasLasIncidencias.filter { incidencia ->
            val cumpleEstado = estadoSeleccionado == "Todas" || incidencia.estado == estadoSeleccionado
            val cumpleAula = aulaSeleccionada == "Todas las aulas" || incidencia.aula_nombre == aulaSeleccionada
            val cumpleUsuario = usuarioSeleccionado == "Todos los usuarios" || incidencia.usuario_nombre == usuarioSeleccionado

            cumpleEstado && cumpleAula && cumpleUsuario
        }

        mostrarIncidencias(filtradas)
    }

    private fun mostrarIncidencias(incidencias: List<Incidencia>) {
        val adapter = IncidenciasRecyclerAdapter(incidencias) { incidencia ->
            val intent = Intent(this@GestionarIncidenciasActivity, DetalleIncidenciaAdminActivity::class.java)
            intent.putExtra("incidencia_id", incidencia.id)
            startActivity(intent)
        }
        incidenciasRecyclerView.adapter = adapter
    }

    override fun onSupportNavigateUp(): Boolean {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
        return true
    }
    override fun onResume() {
        super.onResume()
        cargarTodasIncidencias()  // Recargar cuando vuelves
    }
}