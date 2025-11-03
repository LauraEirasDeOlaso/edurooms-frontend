package com.edurooms.app.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.edurooms.app.R
import com.edurooms.app.data.models.Aula
import android.widget.TextView

class AulasAdapter(
    private val aulas: List<Aula>,
    private val onAulaClick: (Aula) -> Unit
) : RecyclerView.Adapter<AulasAdapter.AulaViewHolder>() {

    inner class AulaViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        private val nombreText: TextView = itemView.findViewById(R.id.nombreText)
        private val capacidadText: TextView = itemView.findViewById(R.id.capacidadText)
        private val estadoText: TextView = itemView.findViewById(R.id.estadoText)

        fun bind(aula: Aula) {
            nombreText.text = aula.nombre
            capacidadText.text = "Capacidad: ${aula.capacidad}"
            estadoText.text = "Estado: ${aula.estado}"

            itemView.setOnClickListener {
                onAulaClick(aula)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AulaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_aula, parent, false)
        return AulaViewHolder(view)
    }

    override fun onBindViewHolder(holder: AulaViewHolder, position: Int) {
        holder.bind(aulas[position])
    }

    override fun getItemCount() = aulas.size
}