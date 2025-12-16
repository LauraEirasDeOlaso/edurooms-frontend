package com.edurooms.app.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.edurooms.app.R

data class HorarioItem(
    val hora_inicio: String,
    val hora_fin: String,
    val libre: Boolean,  // true = disponible, false = ocupado
    var seleccionado: Boolean = false
)

class HorariosAdapter(
    private val horarios: List<HorarioItem>,
    private val onHorarioClick: (HorarioItem) -> Unit
) : RecyclerView.Adapter<HorariosAdapter.HorarioViewHolder>() {

    inner class HorarioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val horarioText: TextView = itemView.findViewById(R.id.horarioText)

        fun bind(horario: HorarioItem) {
            horarioText.text = "${horario.hora_inicio} - ${horario.hora_fin}"

            when {
                !horario.libre -> {
                    // ❌ OCUPADO
                    horarioText.setBackgroundColor(
                        itemView.context.getColor(R.color.text_muted)
                    )
                    horarioText.setTextColor(
                        itemView.context.getColor(R.color.text_secondary)
                    )
                    horarioText.alpha = 0.5f
                    itemView.setOnClickListener(null)
                }

                horario.seleccionado -> {
                    // ⭐ SELECCIONADO
                    horarioText.setBackgroundColor(
                        itemView.context.getColor(R.color.primary)
                    )
                    horarioText.setTextColor(
                        itemView.context.getColor(R.color.text_on_primary)
                    )
                    horarioText.alpha = 1f

                    itemView.setOnClickListener {
                        seleccionarHorario(bindingAdapterPosition)
                    }
                }

                else -> {
                    // ✅ LIBRE
                    horarioText.setBackgroundColor(
                        itemView.context.getColor(R.color.success)
                    )
                    horarioText.setTextColor(
                        itemView.context.getColor(R.color.text_on_primary)
                    )
                    horarioText.alpha = 1f

                    itemView.setOnClickListener {
                        seleccionarHorario(bindingAdapterPosition)
                    }
                }
            }
        }

    }

    private fun seleccionarHorario(posicion: Int) {
        horarios.forEach { it.seleccionado = false }
        horarios[posicion].seleccionado = true

        notifyDataSetChanged()
        onHorarioClick(horarios[posicion])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HorarioViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_horario, parent, false)
        return HorarioViewHolder(view)
    }

    override fun onBindViewHolder(holder: HorarioViewHolder, position: Int) {
        holder.bind(horarios[position])
    }

    override fun getItemCount() = horarios.size
}