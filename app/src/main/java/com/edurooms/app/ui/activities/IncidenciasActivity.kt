package com.edurooms.app.ui.activities

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.edurooms.app.R
import com.edurooms.app.data.models.Incidencia
import com.edurooms.app.data.network.RetrofitClient
import com.edurooms.app.ui.adapters.IncidenciasAdapter
import kotlinx.coroutines.launch

class IncidenciasActivity : AppCompatActivity() {

    private lateinit var incidenciasListView: ListView
    private lateinit var descripcionInput: EditText
    private lateinit var tipoInput: EditText
    private lateinit var reportarButton: Button
    private var incidenciasLista: MutableList<Incidencia> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_incidencias)

        // Vincular vistas
        incidenciasListView = findViewById(R.id.incidenciasListView)
        descripcionInput = findViewById(R.id.descripcionInput)
        tipoInput = findViewById(R.id.tipoInput)
        reportarButton = findViewById(R.id.reportarButton)

        // Click listener
        reportarButton.setOnClickListener { reportarIncidencia() }

        // Cargar incidencias
        cargarIncidencias()
    }

    private fun cargarIncidencias() {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.obtenerIncidencias()

                if (response.isSuccessful && response.body() != null) {
                    incidenciasLista = response.body()!!.toMutableList()

                    val adapter = IncidenciasAdapter(this@IncidenciasActivity, incidenciasLista)
                    incidenciasListView.adapter = adapter
                } else {
                    Toast.makeText(this@IncidenciasActivity, "Error al cargar incidencias", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@IncidenciasActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun reportarIncidencia() {
        val descripcion = descripcionInput.text.toString().trim()
        val tipo = tipoInput.text.toString().trim()

        if (descripcion.isEmpty() || tipo.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                // TODO: Enviar al backend
                Toast.makeText(this@IncidenciasActivity, "✅ Incidencia reportada", Toast.LENGTH_SHORT).show()

                // Limpiar campos
                descripcionInput.setText("")
                tipoInput.setText("")

                // Recargar lista
                cargarIncidencias()

            } catch (e: Exception) {
                Toast.makeText(this@IncidenciasActivity, "❌ Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}