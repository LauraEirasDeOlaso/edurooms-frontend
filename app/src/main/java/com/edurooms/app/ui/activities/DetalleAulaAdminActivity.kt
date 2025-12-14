package com.edurooms.app.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.edurooms.app.R
import com.edurooms.app.data.models.ActualizarAulaRequest
import com.edurooms.app.data.network.RetrofitClient
import com.edurooms.app.data.utils.TokenManager
import kotlinx.coroutines.launch


class DetalleAulaAdminActivity : BaseActivity() {

    private lateinit var tokenManager: TokenManager
    private var aulaId: Int = 0

    private lateinit var nombreText: TextView
    private lateinit var capacidadText: TextView
    private lateinit var ubicacionText: TextView
    private lateinit var estadoText: TextView
    private lateinit var editarButton: Button
    private lateinit var eliminarButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_aula_admin)

        setupToolbar(title = "", showBackButton = true)
        mostrarIconosToolbar(notificaciones = true, perfil = true)
        configurarIconosToolbar(
            onNotificacionesClick = { Toast.makeText(this, "Notificaciones", Toast.LENGTH_SHORT).show() },
            onPerfilClick = { startActivity(Intent(this, PerfilActivity::class.java)) }
        )

        setupBottomNavigation()
        tokenManager = TokenManager(this)

        aulaId = intent.getIntExtra("aula_id", 0)

        nombreText = findViewById(R.id.nombreText)
        capacidadText = findViewById(R.id.capacidadText)
        ubicacionText = findViewById(R.id.ubicacionText)
        estadoText = findViewById(R.id.estadoText)
        editarButton = findViewById(R.id.editarButton)
        eliminarButton = findViewById(R.id.eliminarButton)

        cargarDetalleAula()

        editarButton.setOnClickListener { mostrarDialogoEditar() }
        eliminarButton.setOnClickListener { mostrarDialogoEliminar() }
    }

    private fun cargarDetalleAula() {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.obtenerAula(aulaId)

                if (response.isSuccessful && response.body() != null) {
                    val aula = response.body()!!
                    nombreText.text = getString(R.string.nombre_formato, aula.nombre)
                    capacidadText.text = getString(R.string.capacidad_formato, aula.capacidad)

                    val ubicacion = aula.ubicacion ?: getString(R.string.ubicacion_no_disponible)
                    ubicacionText.text = getString(R.string.ubicacion_formato, ubicacion)

                    estadoText.text = getString(R.string.estado_formato, aula.estado)
                }
            } catch (e: Exception) {
                Toast.makeText(this@DetalleAulaAdminActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun mostrarDialogoEditar() {
        val nombre = nombreText.text.toString().replace("Nombre: ", "")
        val capacidad = capacidadText.text.toString().replace("Capacidad: ", "").replace(" personas", "")
        val ubicacion = ubicacionText.text.toString().replace("Ubicación: ", "")
        val estado = estadoText.text.toString().replace("Estado: ", "")

        val view = layoutInflater.inflate(R.layout.dialog_editar_aula, null)
        val nombreInput = view.findViewById<EditText>(R.id.nombreInput)
        val capacidadInput = view.findViewById<EditText>(R.id.capacidadInput)
        val ubicacionInput = view.findViewById<EditText>(R.id.ubicacionInput)
        val estadoSpinner = view.findViewById<Spinner>(R.id.estadoSpinner)


        nombreInput.setText(nombre)
        capacidadInput.setText(capacidad)
        ubicacionInput.setText(ubicacion)

        // ← Usar view.context en lugar de this
        val estados = resources.getStringArray(R.array.estados_aula).toList()
        val estadoAdapter =
            ArrayAdapter(view.context, android.R.layout.simple_spinner_item, estados)
        estadoSpinner.adapter = estadoAdapter

        val estadoIndex = estados.indexOf(estado)
        if (estadoIndex >= 0) estadoSpinner.setSelection(estadoIndex)

        AlertDialog.Builder(this)
            .setTitle("Editar Aula")
            .setView(view)
            .setPositiveButton("Guardar") { _, _ ->
                editarAula(
                    nombreInput.text.toString(),
                    capacidadInput.text.toString().toIntOrNull() ?: 0,
                    ubicacionInput.text.toString(),
                    estadoSpinner.selectedItem.toString()
                )
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun editarAula(nombre: String, capacidad: Int, ubicacion: String, estado: String) {
        lifecycleScope.launch {
            try {
                val token = tokenManager.obtenerToken() ?: return@launch
                val datos = ActualizarAulaRequest(
                    nombre = nombre,
                    capacidad = capacidad,
                    ubicacion = ubicacion,
                    estado = estado
                )

                val response = RetrofitClient.apiService.actualizarAula("Bearer $token", aulaId, datos)

                if (response.isSuccessful && response.body() != null) {
                    val mensaje = response.body()?.mensaje ?: "✅ Aula actualizada"
                    Toast.makeText(this@DetalleAulaAdminActivity, mensaje, Toast.LENGTH_SHORT).show()
                    cargarDetalleAula()
                } else {
                    Toast.makeText(this@DetalleAulaAdminActivity, "❌ Error al actualizar", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@DetalleAulaAdminActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun mostrarDialogoEliminar() {
        AlertDialog.Builder(this)
            .setTitle("Eliminar Aula")
            .setMessage("¿Estás seguro de que quieres eliminar esta aula?")
            .setPositiveButton("Sí, eliminar") { _, _ -> eliminarAula() }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun eliminarAula() {
        lifecycleScope.launch {
            try {
                val token = tokenManager.obtenerToken() ?: return@launch
                val response = RetrofitClient.apiService.eliminarAula("Bearer $token", aulaId)

                if (response.isSuccessful) {
                    Toast.makeText(this@DetalleAulaAdminActivity, "✅ Aula eliminada", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@DetalleAulaAdminActivity, GestionarAulasActivity::class.java))
                    finish()
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Error desconocido"
                    Toast.makeText(this@DetalleAulaAdminActivity, "❌ $errorMsg", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@DetalleAulaAdminActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        startActivity(Intent(this, GestionarAulasActivity::class.java))
        finish()
        return true
    }
}