package com.edurooms.app.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.edurooms.app.R
import com.edurooms.app.data.models.Aula
import com.edurooms.app.data.network.RetrofitClient
import com.edurooms.app.ui.adapters.AulasAdapter
import kotlinx.coroutines.launch

class ListaAulasActivity : AppCompatActivity() {

    private lateinit var aulasListView: RecyclerView
    private var aulasOriginales: List<Aula> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_aulas)

        // Vincular vista
        aulasListView = findViewById(R.id.aulasListView)
        aulasListView.layoutManager = LinearLayoutManager(this)

        // Cargar aulas
        cargarAulas()
    }

    private fun cargarAulas() {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.obtenerAulas()

                if (response.isSuccessful && response.body() != null) {
                    aulasOriginales = response.body()!!
                    mostrarAulas(aulasOriginales)
                } else {
                    Toast.makeText(this@ListaAulasActivity, "Error al cargar aulas", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@ListaAulasActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun mostrarAulas(aulas: List<Aula>) {
        val adapter = AulasAdapter(aulas) { aula ->
            irADetalleAula(aula.id)
        }
        aulasListView.adapter = adapter
    }

    private fun irADetalleAula(aulaId: Int) {
        val intent = Intent(this, DetalleAulaActivity::class.java)
        intent.putExtra("aula_id", aulaId)
        startActivity(intent)
    }
}