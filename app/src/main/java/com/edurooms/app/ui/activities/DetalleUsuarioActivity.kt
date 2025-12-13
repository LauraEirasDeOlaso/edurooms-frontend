package com.edurooms.app.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.widget.SwitchCompat
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.edurooms.app.R
import com.edurooms.app.data.network.RetrofitClient
import com.edurooms.app.data.utils.TokenManager
import com.edurooms.app.ui.adapters.ReservasAdapter
import kotlinx.coroutines.launch


class DetalleUsuarioActivity : BaseActivity() {

    private var usuarioId: Int = 0
    private var usuarioNombre: String = ""
    private var usuarioEmail: String = ""
    private var usuarioRol: String = ""
    private var usuarioDepartamento: String = ""
    private var usuarioEstado: String = "habilitado"

    private lateinit var tokenManager: TokenManager
    private lateinit var usuarioNombreDetail: TextView
    private lateinit var usuarioEmailDetail: TextView
    private lateinit var usuarioDepartamentoDetail: TextView
    private lateinit var rolSpinner: Spinner
    private lateinit var estadoSwitch: SwitchCompat
    private lateinit var guardarCambiosButton: Button
    private lateinit var eliminarUsuarioButton: Button
    private lateinit var reservasRecyclerView: RecyclerView
    private lateinit var incidenciasRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_usuario)

        // Obtener datos del usuario desde el intent
        usuarioId = intent.getIntExtra("usuario_id", 0)
        usuarioNombre = intent.getStringExtra("usuario_nombre") ?: ""
        usuarioEmail = intent.getStringExtra("usuario_email") ?: ""
        usuarioRol = intent.getStringExtra("usuario_rol") ?: ""
        usuarioEstado = intent.getStringExtra("usuario_estado") ?: "habilitado"
        // usuarioDepartamento = intent.getStringExtra("usuario_departamento") ?: ""

        // Configurar Toolbar
        setupToolbar(title = "Detalle: $usuarioNombre", showBackButton = true)
        mostrarIconosToolbar(notificaciones = true, perfil = true)
        configurarIconosToolbar(
            onNotificacionesClick = { Toast.makeText(this, "Notificaciones", Toast.LENGTH_SHORT).show() },
            onPerfilClick = { startActivity(Intent(this, PerfilActivity::class.java)) }
        )

        // Configurar Bottom Navigation
        setupBottomNavigation()

        // Inicializar TokenManager
        tokenManager = TokenManager(this)

        // Vincular vistas
        usuarioNombreDetail = findViewById(R.id.usuarioNombreDetail)
        usuarioEmailDetail = findViewById(R.id.usuarioEmailDetail)
        usuarioDepartamentoDetail = findViewById(R.id.usuarioDepartamentoDetail)
        rolSpinner = findViewById(R.id.rolSpinner)
        estadoSwitch = findViewById(R.id.estadoSwitch)
        guardarCambiosButton = findViewById(R.id.guardarCambiosButton)
        eliminarUsuarioButton = findViewById(R.id.eliminarUsuarioButton)
        reservasRecyclerView = findViewById(R.id.reservasRecyclerView)
        incidenciasRecyclerView = findViewById(R.id.incidenciasRecyclerView)

        // Configurar RecyclerViews
        reservasRecyclerView.layoutManager = LinearLayoutManager(this)
        //incidenciasRecyclerView.layoutManager = LinearLayoutManager(this)

        // Mostrar datos del usuario
        mostrarDatosUsuario()

        // Cargar datos completos del usuario desde backend
        //cargarDatosUsuarioCompleto()

        // Cargar reservas e incidencias
        cargarReservasUsuario()
        //cargarIncidenciasUsuario()

        // Click listeners
        guardarCambiosButton.setOnClickListener { guardarCambios() }
        eliminarUsuarioButton.setOnClickListener { mostrarDialogoConfirmacionEliminar() }
    }

    private fun mostrarDatosUsuario() {
        usuarioNombreDetail.text = getString(R.string.nombre_formato, usuarioNombre)
        usuarioEmailDetail.text = getString(R.string.email_label_formato, usuarioEmail)
        usuarioDepartamentoDetail.text = getString(R.string.departamento_label_formato, usuarioDepartamento)

        // Configurar Spinner con rol actual
        val rolesArray = resources.getStringArray(R.array.roles_array)
        val rolIndex = rolesArray.indexOf(usuarioRol)
        if (rolIndex >= 0) {
            rolSpinner.setSelection(rolIndex)
        }

        // Configurar Switch con estado actual
        estadoSwitch.isChecked = (usuarioEstado == "habilitado")

    }

    private fun cargarReservasUsuario() {
        lifecycleScope.launch {
            try {
                val tokenManager = TokenManager(this@DetalleUsuarioActivity)
                val token = tokenManager.obtenerToken() ?: return@launch

                val response = RetrofitClient.apiService.obtenerReservasPorUsuario("Bearer $token", usuarioId)

                if (response.isSuccessful && response.body() != null) {
                    val reservas = response.body()!!
                    if (reservas.isNotEmpty()) {
                        val adapter = ReservasAdapter(reservas) { reserva ->
                            // Click en reserva - aquí podrías abrir DetalleReservaActivity
                            val intent = Intent(this@DetalleUsuarioActivity, DetalleReservaActivity::class.java)
                            intent.putExtra("reserva_id", reserva.id)
                            this@DetalleUsuarioActivity.startActivity(intent)
                        }
                        reservasRecyclerView.adapter = adapter
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(this@DetalleUsuarioActivity, "Error cargando reservas: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun cargarIncidenciasUsuario() {
        lifecycleScope.launch {
            try {
                //val token = tokenManager.obtenerToken() ?: ""
                // TODO: Crear endpoint en backend para obtener incidencias de un usuario específico
                // Por ahora, solo mostrar mensaje
                Toast.makeText(this@DetalleUsuarioActivity, "Incidencias del usuario", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this@DetalleUsuarioActivity, "Error cargando incidencias: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun guardarCambios() {
        val nuevoRol = rolSpinner.selectedItem.toString()
        val nuevoEstado = if (estadoSwitch.isChecked) "habilitado" else "deshabilitado"


        lifecycleScope.launch {
            try {
                val token = tokenManager.obtenerToken() ?: ""

                // Llamar al endpoint de editar usuario
                val response = RetrofitClient.apiService.editarUsuario(
                    "Bearer $token",
                    usuarioId,
                    mapOf("rol" to nuevoRol, "estado" to nuevoEstado)
                )

                if (response.isSuccessful) {
                    Toast.makeText(this@DetalleUsuarioActivity, "✅ Cambios guardados", Toast.LENGTH_SHORT).show()
                    usuarioRol = nuevoRol
                    usuarioEstado = nuevoEstado
                    finish()
                } else {
                    Toast.makeText(this@DetalleUsuarioActivity, "❌ Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@DetalleUsuarioActivity, "❌ Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun mostrarDialogoConfirmacionEliminar() {
        AlertDialog.Builder(this)
            .setTitle("Eliminar Usuario")
            .setMessage("¿Estás seguro que quieres eliminar a $usuarioNombre?\n\nEsto solo es posible si no tiene reservas activas.")
            .setPositiveButton("Sí, eliminar") { _, _ ->
                eliminarUsuario()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }

    private fun eliminarUsuario() {
        lifecycleScope.launch {
            try {
                val token = tokenManager.obtenerToken() ?: ""

                val response = RetrofitClient.apiService.eliminarUsuario("Bearer $token", usuarioId)

                if (response.isSuccessful) {
                    Toast.makeText(this@DetalleUsuarioActivity, "✅ Usuario eliminado", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    // Intentar parsear error del backend
                    try {
                        val errorBody = response.errorBody()?.string()
                        val errorJson = org.json.JSONObject(errorBody ?: "{}")
                        val errorMsg = errorJson.optString("mensaje", "Error desconocido")
                        Toast.makeText(this@DetalleUsuarioActivity, "❌ $errorMsg", Toast.LENGTH_SHORT).show()
                    } catch (_: Exception) {
                        Toast.makeText(this@DetalleUsuarioActivity, "❌ Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(this@DetalleUsuarioActivity, "❌ Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun cargarDatosUsuarioCompleto() {
        lifecycleScope.launch {
            try {
                val token = tokenManager.obtenerToken() ?: ""
                val response = RetrofitClient.apiService.obtenerUsuarioPorId("Bearer $token", usuarioId)

                if (response.isSuccessful && response.body() != null) {
                    val usuarioData = response.body()!!["usuario"] as? Map<*, *>
                    if (usuarioData != null) {
                        usuarioNombre = usuarioData["nombre"].toString()
                        usuarioEmail = usuarioData["email"].toString()
                        usuarioRol = usuarioData["rol"].toString()
                        usuarioDepartamento = usuarioData["departamento"].toString()
                        usuarioEstado = usuarioData["estado"].toString()

                        mostrarDatosUsuario()
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(this@DetalleUsuarioActivity, "Error cargando datos: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}