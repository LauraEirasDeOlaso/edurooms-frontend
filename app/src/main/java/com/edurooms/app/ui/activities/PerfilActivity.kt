package com.edurooms.app.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.edurooms.app.R
import com.edurooms.app.data.network.RetrofitClient
import com.edurooms.app.data.utils.TokenManager
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody

class PerfilActivity : BaseActivity() {

    private lateinit var tokenManager: TokenManager
    private lateinit var userNameText: TextView
    private lateinit var userEmailText: TextView
    private lateinit var userRoleText: TextView
    private lateinit var userDepartmentText: TextView
    private lateinit var changePasswordButton: Button
    private lateinit var notificationsButton: Button
    private lateinit var logoutButton: Button

    private lateinit var profileImageContainer: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        // Configurar Toolbar con botón atrás
        setupToolbar(title = "Perfil", showBackButton = true)
        mostrarIconosToolbar(notificaciones = true, perfil = false)

        configurarIconosToolbar(
            onNotificacionesClick = { Toast.makeText(this, "Notificaciones", Toast.LENGTH_SHORT).show() }
        )

        // Configurar Bottom Navigation
        setupBottomNavigation()
        seleccionarItemBottomNav(R.id.nav_perfil)

        // Inicializar TokenManager
        tokenManager = TokenManager(this)

        // Vincular vistas
        userNameText = findViewById(R.id.userNameText)
        userEmailText = findViewById(R.id.userEmailText)
        userRoleText = findViewById(R.id.userRoleText)
        userDepartmentText = findViewById(R.id.userDepartmentText)
        changePasswordButton = findViewById(R.id.changePasswordButton)
        notificationsButton = findViewById(R.id.notificationsButton)
        logoutButton = findViewById(R.id.logoutButton)
        profileImageContainer = findViewById(R.id.profileImageContainer)

        // Cargar datos del usuario
        cargarDatosUsuario()
        // Cargar foto guardada
        cargarFotoPerfil()

        // Click listeners
        changePasswordButton.setOnClickListener { irACambiarContrasena() }
        notificationsButton.setOnClickListener { irANotificaciones() }
        logoutButton.setOnClickListener { mostrarDialogoConfirmacionLogout() }
        // Para subir foto
        profileImageContainer.setOnClickListener { abrirGaleria() }
    }

    private fun cargarDatosUsuario() {
        val nombre = tokenManager.obtenerNombre() ?: "Usuario"
        val email = tokenManager.obtenerEmail() ?: ""
        val rol = tokenManager.obtenerRol()
        val departamento = tokenManager.obtenerDepartamento() ?: "No asignado"

        userNameText.text = nombre.ifEmpty { "Usuario" }
        userEmailText.text = email
        userRoleText.text = rol.replaceFirstChar { it.uppercase() }
        userDepartmentText.text = departamento
    }

    private fun irACambiarContrasena() {
        startActivity(Intent(this, CambiarPasswordActivity::class.java))
    }

    private fun irANotificaciones() {
        Toast.makeText(this, "Notificaciones - Próximamente", Toast.LENGTH_SHORT).show()
        // TODO: Crear NotificacionesActivity
    }

    private fun mostrarDialogoConfirmacionLogout() {
        AlertDialog.Builder(this)
            .setTitle("Cerrar Sesión")
            .setMessage("¿Estás seguro que quieres cerrar sesión?")
            .setPositiveButton("Sí, cerrar") { _, _ ->
                cerrarSesion()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }

    private fun cerrarSesion() {
        tokenManager.eliminarToken()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun abrirGaleria() {
        val intent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == android.app.Activity.RESULT_OK && data != null) {
            val uri = data.data
            if (uri != null) {
                subirFoto(uri)
            }
        }
    }

    private fun subirFoto(uri: android.net.Uri) {
        lifecycleScope.launch {
            try {
                val token = tokenManager.obtenerToken() ?: return@launch
                val usuarioId = tokenManager.obtenerIdUsuario()

                // Convertir URI a File
                val inputStream = contentResolver.openInputStream(uri) ?: return@launch
                val file = java.io.File(cacheDir, "foto_perfil.jpg")
                file.outputStream().use { inputStream.copyTo(it) }
                inputStream.close()

                // Crear MultipartBody.Part
                val requestBody = file.asRequestBody("image/jpeg".toMediaType())
                val fotoPart = okhttp3.MultipartBody.Part.createFormData("foto", file.name, requestBody)

                val response = RetrofitClient.apiService.subirFotoPerfil("Bearer $token", usuarioId, fotoPart)

                if (response.isSuccessful) {
                    Toast.makeText(this@PerfilActivity, "✅ Foto actualizada", Toast.LENGTH_SHORT).show()
                    cargarFotoPerfil()
                } else {
                    Toast.makeText(this@PerfilActivity, "❌ Error al subir foto", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@PerfilActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun cargarFotoPerfil() {
        lifecycleScope.launch {
            try {
                val usuarioId = tokenManager.obtenerIdUsuario()
                val response = RetrofitClient.apiService.obtenerFotoPerfil(usuarioId)

                if (response.isSuccessful) {
                    val fotoRuta = response.body()?.get("foto_ruta") as? String
                    if (fotoRuta != null) {
                        val baseUrl = "http://192.168.1.32:3000/" // ← CAMBIAR IP
                        val urlFoto = baseUrl + fotoRuta

                        // ← AGREGAR LOG
                        android.util.Log.d("FOTO_DEBUG", "URL: $urlFoto")
                        android.util.Log.d("FOTO_DEBUG", "fotoRuta: $fotoRuta")

                        // Cargar con Glide
                        val profileImage = findViewById<ImageView>(R.id.profileImage)
                        com.bumptech.glide.Glide.with(this@PerfilActivity)
                            .load(urlFoto)
                            .circleCrop()
                            .listener(object : com.bumptech.glide.request.RequestListener<android.graphics.drawable.Drawable> {
                                override fun onLoadFailed(e: com.bumptech.glide.load.engine.GlideException?, model: Any?, target: com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable>?, isFirstResource: Boolean): Boolean {
                                    android.util.Log.e("GLIDE_ERROR", "Error cargando: ${e?.message}")
                                    return false
                                }
                                override fun onResourceReady(resource: android.graphics.drawable.Drawable?, model: Any?, target: com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable>?, dataSource: com.bumptech.glide.load.DataSource?, isFirstResource: Boolean): Boolean {
                                    android.util.Log.d("GLIDE_SUCCESS", "Imagen cargada")
                                    return false
                                }
                            })
                            .into(profileImage)
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("PERFIL", "Error cargando foto", e)
            }
        }
    }

}