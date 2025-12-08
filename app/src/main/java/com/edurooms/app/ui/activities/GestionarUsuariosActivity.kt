package com.edurooms.app.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.edurooms.app.R
import com.edurooms.app.data.models.UsuarioData
import com.edurooms.app.data.network.RetrofitClient
import com.edurooms.app.data.utils.TokenManager
import com.edurooms.app.ui.adapters.UsuariosAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class GestionarUsuariosActivity : BaseActivity() {

    private lateinit var usuariosRecyclerView: RecyclerView
    private lateinit var tokenManager: TokenManager
    private var usuariosList: List<UsuarioData> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gestionar_usuarios)

        // Configurar Toolbar
        setupToolbar(title = "Gestionar Usuarios", showBackButton = true)
        mostrarIconosToolbar(notificaciones = true, perfil = true)
        configurarIconosToolbar(
            onNotificacionesClick = { Toast.makeText(this, "Notificaciones", Toast.LENGTH_SHORT).show() },
            onPerfilClick = { startActivity(Intent(this, PerfilActivity::class.java)) }
        )

        // Configurar Bottom Navigation
        setupBottomNavigation()


        // Inicializar TokenManager
        tokenManager = TokenManager(this)

        // Vincular RecyclerView
        usuariosRecyclerView = findViewById(R.id.usuariosRecyclerView)
        usuariosRecyclerView.layoutManager = LinearLayoutManager(this)

        val crearUsuarioFab = findViewById<FloatingActionButton>(R.id.crearUsuarioFab)
        crearUsuarioFab.setOnClickListener {
            startActivity(Intent(this, CrearUsuarioActivity::class.java))
        }

        // Cargar usuarios
        cargarUsuarios()
    }

    // Refrescar la lista
    override fun onResume() {
        super.onResume()
        cargarUsuarios()
    }

    private fun cargarUsuarios() {
        lifecycleScope.launch {
            try {
                val token = tokenManager.obtenerToken() ?: ""
                val response = RetrofitClient.apiService.obtenerUsuarios("Bearer $token")

                if (response.isSuccessful && response.body() != null) {
                    usuariosList = response.body()!!.usuarios
                    mostrarUsuarios(usuariosList)
                } else {
                    Toast.makeText(
                        this@GestionarUsuariosActivity,
                        "Error al cargar usuarios: ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@GestionarUsuariosActivity,
                    "Error: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun mostrarUsuarios(usuarios: List<UsuarioData>) {
        val adapter = UsuariosAdapter(usuarios) { usuario ->
            irADetalleUsuario(usuario)
        }
        usuariosRecyclerView.adapter = adapter
    }

    private fun irADetalleUsuario(usuario: UsuarioData) {
        val intent = Intent(this, DetalleUsuarioActivity::class.java)
        intent.putExtra("usuario_id", usuario.id)
        intent.putExtra("usuario_nombre", usuario.nombre)
        intent.putExtra("usuario_email", usuario.email)
        intent.putExtra("usuario_rol", usuario.rol)
        intent.putExtra("usuario_estado", usuario.estado ?: "habilitado")
        //intent.putExtra("usuario_departamento", usuario.departamento ?: "")
        startActivity(intent)
    }


}