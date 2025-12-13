package com.edurooms.app.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.edurooms.app.R
import com.edurooms.app.data.models.Reserva



class ReservasAdapter(
    private val reservas: List<Reserva>,
    private val onItemClick: (Reserva) -> Unit  // ← Callback de click
) : RecyclerView.Adapter<ReservasAdapter.ReservaViewHolder>() {

    inner class ReservaViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        private val aulaNombreText: TextView = itemView.findViewById(R.id.aulaNombreText)
        private val fechaText: TextView = itemView.findViewById(R.id.fechaText)
        private val horaText: TextView = itemView.findViewById(R.id.horaText)
        private val estadoText: TextView = itemView.findViewById(R.id.estadoText)

        private val usuarioText: TextView = itemView.findViewById(R.id.usuarioText)

        fun bind(reserva: Reserva) {
            aulaNombreText.text = reserva.aula_nombre

            // Parsear fecha manualmente sin problemas de zona horaria
            val partes = reserva.fecha.take(10).split("-")
            val fechaFormateada = "${partes[2]}/${partes[1]}/${partes[0]}"

            android.util.Log.d("RESERVA_DEBUG", "Partes: $partes -> $fechaFormateada")

            fechaText.text = itemView.context.getString(R.string.fecha_formato, fechaFormateada)
            horaText.text = itemView.context.getString(R.string.hora_formato, reserva.hora_inicio, reserva.hora_fin)
            estadoText.text = itemView.context.getString(R.string.estado_formato, reserva.estado)
            usuarioText.text = itemView.context.getString(R.string.profesor_formato, reserva.usuario_nombre ?: "Desconocido")

            // Color según estado
            when (reserva.estado) {
                "confirmada" -> {
                    estadoText.setTextColor(android.graphics.Color.GREEN)
                }
                "completada" -> {
                    estadoText.setTextColor(android.graphics.Color.GRAY)
                }
                "cancelada" -> {
                    estadoText.setTextColor(android.graphics.Color.RED)
                } else ->
                estadoText.setTextColor(android.graphics.Color.BLACK)
            }

            // Click listener
            itemView.setOnClickListener { onItemClick(reserva) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReservaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_reserva, parent, false)
        return ReservaViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReservaViewHolder, position: Int) {
        holder.bind(reservas[position])
    }

    override fun getItemCount() = reservas.size

}
