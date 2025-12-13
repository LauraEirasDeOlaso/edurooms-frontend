package com.edurooms.app.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.edurooms.app.R
import com.edurooms.app.data.network.RetrofitClient
import com.edurooms.app.data.utils.TokenManager
import com.edurooms.app.ui.adapters.ReservasAdapter
import kotlinx.coroutines.launch

class MisReservasActivity : BaseActivity() {

    private lateinit var reservasRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mis_reservas)

        // ← AGREGAR ESTO:
        setupToolbar(title = "", showBackButton = true)
        mostrarIconosToolbar(perfil = true, notificaciones = true)

        configurarIconosToolbar(
            onNotificacionesClick = {
                Toast.makeText(this, "Notificaciones", Toast.LENGTH_SHORT).show()
            },
            onPerfilClick = { startActivity(Intent(this, PerfilActivity::class.java)) }
        )

        setupBottomNavigation()
        seleccionarItemBottomNav(R.id.nav_reservas)

        // Crear Reserva
        val fabCrearReserva = findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fabCrearReserva)
        fabCrearReserva.setOnClickListener {
            startActivity(Intent(this, ListaAulasActivity::class.java))
        }

        reservasRecyclerView = findViewById(R.id.reservasRecyclerView)
        reservasRecyclerView.layoutManager = LinearLayoutManager(this)

        cargarMisReservas()
    }

    private fun cargarMisReservas() {
        lifecycleScope.launch {
            try {
                val tokenManager = TokenManager(this@MisReservasActivity)
                val token = tokenManager.obtenerToken()

                if (token == null) {
                    Toast.makeText(this@MisReservasActivity, "❌ No hay sesión", Toast.LENGTH_SHORT)
                        .show()
                    return@launch
                }

                val response = RetrofitClient.apiService.obtenerMisReservas("Bearer $token")

                if (response.isSuccessful && response.body() != null) {
                    val reservas = response.body()!!
                    if (reservas.isEmpty()) {
                        Toast.makeText(
                            this@MisReservasActivity,
                            "No tienes reservas",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val adapter = ReservasAdapter(reservas) { reserva ->
                            val intent =
                                Intent(this@MisReservasActivity, DetalleReservaActivity::class.java)
                            intent.putExtra("reserva_id", reserva.id)
                            startActivity(intent)
                        }
                        reservasRecyclerView.adapter = adapter
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(this@MisReservasActivity, "Error: ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}