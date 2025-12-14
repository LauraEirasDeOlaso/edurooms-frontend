package com.edurooms.app.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.edurooms.app.R
import com.edurooms.app.data.models.Incidencia
import android.graphics.Color

class IncidenciasRecyclerAdapter(
    private val incidencias: List<Incidencia>,
    private val onItemClick: (Incidencia) -> Unit
) : RecyclerView.Adapter<IncidenciasRecyclerAdapter.IncidenciaViewHolder>() {

    inner class IncidenciaViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        private val aulaText: TextView = itemView.findViewById(R.id.aulaText)
        private val descripcionText: TextView = itemView.findViewById(R.id.descripcionText)
        private val estadoText: TextView = itemView.findViewById(R.id.estadoText)

        private val usuarioText: TextView = itemView.findViewById(R.id.usuarioText)

        fun bind(incidencia: Incidencia) {
            aulaText.text = incidencia.aula_nombre ?: "Aula desconocida"
            descripcionText.text = incidencia.descripcion
            usuarioText.text = "Reportado por: ${incidencia.usuario_nombre ?: "Desconocido"}"
            estadoText.text = incidencia.estado

            // Colores por estado
            when (incidencia.estado) {
                "pendiente" -> estadoText.setTextColor(itemView.context.getColor(R.color.warning))
                "en_revision" -> estadoText.setTextColor(itemView.context.getColor(R.color.primary))
                "resuelta" -> estadoText.setTextColor(itemView.context.getColor(R.color.success))
                else -> estadoText.setTextColor(itemView.context.getColor(R.color.text_secondary))
            }

            itemView.setOnClickListener { onItemClick(incidencia) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IncidenciaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_incidencia, parent, false)
        return IncidenciaViewHolder(view)
    }

    override fun onBindViewHolder(holder: IncidenciaViewHolder, position: Int) {
        holder.bind(incidencias[position])
    }

    override fun getItemCount() = incidencias.size
}