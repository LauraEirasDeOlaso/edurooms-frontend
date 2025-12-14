package com.edurooms.app.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.edurooms.app.R
import com.edurooms.app.data.models.CrearIncidenciaRequest
import com.edurooms.app.data.models.Incidencia
import com.edurooms.app.data.network.RetrofitClient
import com.edurooms.app.data.utils.TokenManager
import com.edurooms.app.ui.adapters.IncidenciasRecyclerAdapter
import kotlinx.coroutines.launch

class IncidenciasActivity : BaseActivity() {

    private lateinit var incidenciasRecyclerView: RecyclerView
    private lateinit var incidenciasAdapter: IncidenciasRecyclerAdapter
    private lateinit var descripcionInput: EditText
    private lateinit var tipoSpinner: Spinner
    private lateinit var reportarButton: Button
    private var incidenciasLista: MutableList<Incidencia> = mutableListOf()

    private var aulaIdRecibido: Int = 0

    private lateinit var aulasSpinner: Spinner

    private var aulasLista: List<com.edurooms.app.data.models.Aula> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_incidencias)

        setupToolbar(title = "Reportar Incidencia", showBackButton = true)
        mostrarIconosToolbar(notificaciones = true, perfil = true)
        configurarIconosToolbar(
            onNotificacionesClick = { Toast.makeText(this, "Notificaciones", Toast.LENGTH_SHORT).show() },
            onPerfilClick = { startActivity(Intent(this, PerfilActivity::class.java)) }
        )

        // Configurar Bottom Navigation
        setupBottomNavigation()
        seleccionarItemBottomNav(R.id.nav_incidencias)

        aulaIdRecibido = intent.getIntExtra("aula_id", 0)

        // Vincular vistas
        incidenciasRecyclerView = findViewById(R.id.incidenciasRecyclerView)
        incidenciasRecyclerView.layoutManager = LinearLayoutManager(this)
        descripcionInput = findViewById(R.id.descripcionInput)
        tipoSpinner = findViewById(R.id.tipoSpinner)
        reportarButton = findViewById(R.id.reportarButton)
        aulasSpinner = findViewById(R.id.aulasSpinner)

        cargarAulasEnSpinner()

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

                        incidenciasAdapter = IncidenciasRecyclerAdapter(incidenciasLista) { incidencia ->
                            val intent = Intent(this@IncidenciasActivity, DetalleIncidenciaActivity::class.java)
                            intent.putExtra("incidencia_id", incidencia.id)
                            startActivity(intent)
                        }
                        incidenciasRecyclerView.adapter = incidenciasAdapter
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

    override fun onResume() {
        super.onResume()
        cargarIncidencias()  // Se ejecuta automáticamente cuando vuelves
    }

    private fun reportarIncidencia() {
        val descripcion = descripcionInput.text.toString().trim()
        val tipo = tipoSpinner.selectedItem.toString()

        if (descripcion.isEmpty() || tipo.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        // como en back
        if (descripcion.length < 10) {
            Toast.makeText(this, "La descripción debe tener al menos 10 caracteres", Toast.LENGTH_SHORT).show()
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
                    tipoSpinner.setSelection(0)

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

    override fun onSupportNavigateUp(): Boolean {
        if (aulaIdRecibido != 0) {
            // Vino de DetalleAulaActivity
            finish()
        } else {
            // Vino de Bottom Nav
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        return true
    }

    private fun cargarAulasEnSpinner() {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.obtenerAulas()
                if (response.isSuccessful && response.body() != null) {
                    aulasLista = response.body()!!
                    val aulasNames = aulasLista.map { it.nombre }

                    val adapter = ArrayAdapter(
                        this@IncidenciasActivity,
                        android.R.layout.simple_spinner_item,
                        aulasNames
                    )
                    adapter.setDropDownViewResource(R.layout.custom_spinner_item)
                    aulasSpinner.adapter = adapter

                    // ← AGREGAR LISTENER AQUÍ
                    aulasSpinner.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: android.widget.AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                            aulaIdRecibido = aulasLista[position].id
                            cargarIncidencias() // Recargar cuando cambia aula
                        }
                        override fun onNothingSelected(parent: android.widget.AdapterView<*>) {}
                    }

                    // Si vino con aula_id, seleccionar esa aula
                    if (aulaIdRecibido != 0) {
                        val index = aulasLista.indexOfFirst { it.id == aulaIdRecibido }
                        if (index >= 0) aulasSpinner.setSelection(index)
                    }
                }
            } catch (_: Exception) {
                Toast.makeText(this@IncidenciasActivity, "Error cargando aulas", Toast.LENGTH_SHORT).show()
            }
        }
    }

}