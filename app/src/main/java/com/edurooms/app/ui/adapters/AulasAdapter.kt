package com.edurooms.app.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.edurooms.app.R
import com.edurooms.app.data.models.Aula

class AulasAdapter(
    private val mContext: Context,
    private val aulas: List<Aula>,
    private val onAulaClick: (Aula) -> Unit
) : ArrayAdapter<Aula>(mContext, 0, aulas) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(mContext)
            .inflate(R.layout.item_aula, parent, false)

        val aula = aulas[position]

        val nombreText: TextView = view.findViewById(R.id.nombreText)
        val capacidadText: TextView = view.findViewById(R.id.capacidadText)
        val estadoText: TextView = view.findViewById(R.id.estadoText)

        nombreText.text = aula.nombre
        capacidadText.text = "Capacidad: ${aula.capacidad}"
        estadoText.text = "Estado: ${aula.estado}"

        view.setOnClickListener {
            onAulaClick(aula)
        }

        return view
    }
}