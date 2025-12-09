package com.edurooms.app.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.edurooms.app.R
import com.edurooms.app.data.models.Reserva
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.text.SimpleDateFormat
import java.util.Locale


class ReservasAdapter(
    private val reservas: List<Reserva>,
    private val onItemClick: (Reserva) -> Unit  // ← Callback de click
) : RecyclerView.Adapter<ReservasAdapter.ReservaViewHolder>() {

    inner class ReservaViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        private val aulaNombreText: TextView = itemView.findViewById(R.id.aulaNombreText)
        private val fechaText: TextView = itemView.findViewById(R.id.fechaText)
        private val horaText: TextView = itemView.findViewById(R.id.horaText)
        private val estadoText: TextView = itemView.findViewById(R.id.estadoText)

        fun bind(reserva: Reserva) {
            aulaNombreText.text = reserva.aula_nombre

            // Convertir fecha de YYYY-MM-DD a dd/MM/yyyy
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale("es", "ES"))
            val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale("es", "ES"))

            val fechaParsed = inputFormat.parse(reserva.fecha.substring(0, 10))
            val fechaFormateada = outputFormat.format(fechaParsed)
            fechaText.text = "Fecha: $fechaFormateada"

            horaText.text = "${reserva.hora_inicio} - ${reserva.hora_fin}"
            estadoText.text = "Estado: ${reserva.estado}"

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
