package com.edurooms.app.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.edurooms.app.R
import com.edurooms.app.data.network.RetrofitClient
import kotlinx.coroutines.launch

class DetalleAulaActivity : BaseActivity() {

    private lateinit var nombreText: TextView
    private lateinit var capacidadText: TextView
    private lateinit var ubicacionText: TextView
    private lateinit var estadoText: TextView
    private lateinit var reservarButton: Button
    private lateinit var incidenciaButton: Button

    private var aulaId: Int = 0

    private lateinit var incidenciasListView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        android.util.Log.d("DETALLE", "onCreate iniciado")



        setContentView(R.layout.activity_detalle_aula)
        android.util.Log.d("DETALLE", "Layout seteado")

        setupToolbar(title = "", showBackButton = true)
        mostrarIconosToolbar(notificaciones = true, perfil = true)
        configurarIconosToolbar(
            onNotificacionesClick = { Toast.makeText(this, "Notificaciones", Toast.LENGTH_SHORT).show() },
            onPerfilClick = { startActivity(Intent(this, PerfilActivity::class.java)) }
        )

        setupBottomNavigation()
        // NO llamar a :
        //seleccionarItemBottomNav(R.id.nav_aulas)

        // Obtener aula_id del Intent
        aulaId = intent.getIntExtra("aula_id", 0)
        android.util.Log.d("DETALLE", "aulaId obtenido: $aulaId")

        // Vincular vistas
        try {
            nombreText = findViewById(R.id.nombreText)
            android.util.Log.d("DETALLE", "nombreText vinculado")

            capacidadText = findViewById(R.id.capacidadText)
            ubicacionText = findViewById(R.id.ubicacionText)
            estadoText = findViewById(R.id.estadoText)
            reservarButton = findViewById(R.id.reservarButton)
            incidenciaButton = findViewById(R.id.incidenciaButton)
            incidenciasListView = findViewById(R.id.incidenciasListView)

            android.util.Log.d("DETALLE", "Todas las vistas vinculadas")
        } catch (e: Exception) {
            android.util.Log.e("DETALLE", "Error vinculando vistas", e)
            e.printStackTrace()
            return
        }

        // Cargar datos del aula
        cargarDetalleAula()

        // Click listeners
        reservarButton.setOnClickListener {
            irAReserva()
        }
        incidenciaButton.setOnClickListener {
            val nuevoIntent = Intent(this, IncidenciasActivity::class.java)
            nuevoIntent.putExtra("aula_id", aulaId)
            startActivity(nuevoIntent)
        }
    }

    private fun cargarDetalleAula() {
        lifecycleScope.launch {
            try {
                android.util.Log.d("DETALLE", "Intentando obtener aula: $aulaId")
                val response = RetrofitClient.apiService.obtenerAula(aulaId)

                android.util.Log.d("DETALLE", "Response recibido: ${response.code()}")

                if (response.isSuccessful && response.body() != null) {
                    val aula = response.body()!!
                    android.util.Log.d("DETALLE", "Aula: ${aula.nombre}")

                    nombreText.text = aula.nombre
                    capacidadText.text = getString(R.string.capacidad_formato, aula.capacidad)
                    ubicacionText.text = getString(R.string.ubicacion_formato, aula.ubicacion)
                    estadoText.text = getString(R.string.estado_formato, aula.estado)

                    cargarIncidenciasAula()
                } else {
                    android.util.Log.d("DETALLE", "Response no exitoso: ${response.code()}")
                    Toast.makeText(this@DetalleAulaActivity, "Error al cargar aula", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                android.util.Log.e("DETALLE", "Exception completa", e)
                e.printStackTrace()
                Toast.makeText(this@DetalleAulaActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun irAReserva() {
        val intent = Intent(this, ReservaActivity::class.java)
        intent.putExtra("aula_id", aulaId)
        startActivity(intent)
    }

    private fun cargarIncidenciasAula() {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.obtenerIncidenciasAula(aulaId)

                if (response.isSuccessful && response.body() != null) {
                    val incidencias = response.body()!!
                    if (incidencias.isNotEmpty()) {
                        val adapter = com.edurooms.app.ui.adapters.IncidenciasAdapter(
                            this@DetalleAulaActivity,
                            incidencias
                        )
                        incidenciasListView.adapter = adapter
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("DETALLE", "Error cargando incidencias", e)
            }
        }
    }
}