package com.edurooms.app.ui.activities


import android.content.Intent
import android.os.Bundle
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.edurooms.app.R
import com.edurooms.app.data.models.Aula
import com.edurooms.app.data.network.RetrofitClient
import com.edurooms.app.ui.adapters.AulasAdapter
import kotlinx.coroutines.launch

class ListaAulasActivity : AppCompatActivity() {

    private lateinit var aulasListView: ListView
    private var aulasLista: MutableList<Aula> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_aulas)

        aulasListView = findViewById(R.id.aulasListView)

        cargarAulas()
    }

    private fun cargarAulas() {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.obtenerAulas()

                if (response.isSuccessful && response.body() != null) {
                    aulasLista = response.body()!!.toMutableList()

                    val adapter = AulasAdapter(this@ListaAulasActivity, aulasLista) { aula ->
                        irAlDetalle(aula)
                    }
                    aulasListView.adapter = adapter
                } else {
                    Toast.makeText(this@ListaAulasActivity, "Error al cargar aulas", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@ListaAulasActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun irAlDetalle(aula: Aula) {
        val intent = Intent(this@ListaAulasActivity, DetalleAulaActivity::class.java)
        intent.putExtra("aula_id", aula.id)
        intent.putExtra("aula_nombre", aula.nombre)
        intent.putExtra("aula_capacidad", aula.capacidad)
        intent.putExtra("aula_ubicacion", aula.ubicacion)
        intent.putExtra("aula_estado", aula.estado)
        startActivity(intent)
    }
}