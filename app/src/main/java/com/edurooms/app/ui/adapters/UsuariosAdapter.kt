package com.edurooms.app.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.edurooms.app.R
import com.edurooms.app.data.models.UsuarioData

class UsuariosAdapter(
    private val usuarios: List<UsuarioData>,
    private val onUsuarioClick: (UsuarioData) -> Unit
) : RecyclerView.Adapter<UsuariosAdapter.UsuarioViewHolder>() {

    inner class UsuarioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nombreText: TextView = itemView.findViewById(R.id.usuarioNombre)
        private val emailText: TextView = itemView.findViewById(R.id.usuarioEmail)
        private val rolText: TextView = itemView.findViewById(R.id.usuarioRol)
        private val estadoText: TextView = itemView.findViewById(R.id.usuarioEstado)

        fun bind(usuario: UsuarioData) {
            nombreText.text = usuario.nombre
            emailText.text = usuario.email
            rolText.text = "Rol: ${usuario.rol}"
            estadoText.text = "Estado: ${usuario.estado ?: "habilitado"}"

            itemView.setOnClickListener {
                onUsuarioClick(usuario)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsuarioViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_usuario, parent, false)
        return UsuarioViewHolder(view)
    }

    override fun onBindViewHolder(holder: UsuarioViewHolder, position: Int) {
        holder.bind(usuarios[position])
    }

    override fun getItemCount(): Int = usuarios.size
}