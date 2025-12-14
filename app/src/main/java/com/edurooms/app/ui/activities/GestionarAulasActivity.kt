package com.edurooms.app.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.edurooms.app.R
import com.edurooms.app.data.network.RetrofitClient
import com.edurooms.app.data.utils.TokenManager
import com.edurooms.app.ui.adapters.AulasAdapter
import kotlinx.coroutines.launch


class GestionarAulasActivity : BaseActivity() {

    private lateinit var tokenManager: TokenManager
    private lateinit var aulasRecyclerView: RecyclerView

    private lateinit var crearAulaFab: com.google.android.material.floatingactionbutton.FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gestionar_aulas)

        setupToolbar(title = "", showBackButton = true)
        mostrarIconosToolbar(notificaciones = true, perfil = true)
        configurarIconosToolbar(
            onNotificacionesClick = {
                Toast.makeText(this, "Notificaciones", Toast.LENGTH_SHORT).show()
            },
            onPerfilClick = { startActivity(Intent(this, PerfilActivity::class.java)) }
        )

        setupBottomNavigation()
        tokenManager = TokenManager(this)

        aulasRecyclerView = findViewById(R.id.aulasRecyclerView)
        crearAulaFab = findViewById(R.id.crearAulaFab)

        crearAulaFab.setOnClickListener {
            startActivity(Intent(this, CrearAulaActivity::class.java))
        }

        aulasRecyclerView.layoutManager = LinearLayoutManager(this)


        cargarAulas()
    }

    private fun cargarAulas() {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.obtenerAulas()

                if (response.isSuccessful && response.body() != null) {
                    val aulas = response.body()!!
                    if (aulas.isNotEmpty()) {
                        val adapter = AulasAdapter(aulas) { aula ->
                            val intent = Intent(
                                this@GestionarAulasActivity,
                                DetalleAulaAdminActivity::class.java
                            )
                            intent.putExtra("aula_id", aula.id)
                            startActivity(intent)
                        }
                        aulasRecyclerView.adapter = adapter
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@GestionarAulasActivity,
                    "Error: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        cargarAulas() // Recargar cuando vuelves
    }

    override fun onSupportNavigateUp(): Boolean {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
        return true
    }
}