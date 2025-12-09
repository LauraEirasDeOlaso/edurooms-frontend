package com.edurooms.app.ui.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.edurooms.app.R

data class DiaCalendario(
    val dia: Int,
    val esDelMes: Boolean,
    val estado: EstadoDia
)

enum class EstadoDia {
    DISPONIBLE,
    LLENO,
    NO_DISPONIBLE
}

class CalendarAdapter(
    private val dias: List<DiaCalendario>,
    private val onDiaClick: (Int) -> Unit
) : RecyclerView.Adapter<CalendarAdapter.DiaViewHolder>() {

    inner class DiaViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        private val dayContainer: LinearLayout = itemView.findViewById(R.id.dayContainer)
        private val dayText: TextView = itemView.findViewById(R.id.dayText)

        fun bind(dia: DiaCalendario) {
            dayText.text = dia.dia.toString()

            when {
                !dia.esDelMes -> {
                    dayContainer.setBackgroundColor(Color.TRANSPARENT)
                    dayText.setTextColor(Color.GRAY)
                    dayContainer.isClickable = false
                }
                dia.estado == EstadoDia.DISPONIBLE -> {
                    dayContainer.setBackgroundColor(itemView.context.getColor(R.color.primary))
                    dayText.setTextColor(Color.WHITE)
                    dayContainer.isClickable = true
                    dayContainer.setOnClickListener { onDiaClick(dia.dia) }
                }
                dia.estado == EstadoDia.LLENO -> {
                    dayContainer.setBackgroundColor(Color.LTGRAY)
                    dayText.setTextColor(Color.DKGRAY)
                    dayContainer.isClickable = false
                }
                else -> {
                    dayContainer.setBackgroundColor(Color.WHITE)
                    dayText.setTextColor(Color.GRAY)
                    dayContainer.isClickable = false
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_calendar_day, parent, false) as LinearLayout
        return DiaViewHolder(view)
    }

    override fun onBindViewHolder(holder: DiaViewHolder, position: Int) {
        holder.bind(dias[position])
    }

    override fun getItemCount() = dias.size
}
