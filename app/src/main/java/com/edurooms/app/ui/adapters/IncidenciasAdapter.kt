package com.edurooms.app.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.edurooms.app.R
import com.edurooms.app.data.models.Incidencia

class IncidenciasAdapter(
    private val mContext: Context,
    private val incidencias: List<Incidencia>
) : ArrayAdapter<Incidencia>(mContext, 0, incidencias) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(mContext)
            .inflate(R.layout.item_incidencia, parent, false)

        val incidencia = incidencias[position]

        val aulaText: TextView = view.findViewById(R.id.aulaText)
        val descripcionText: TextView = view.findViewById(R.id.descripcionText)
        val estadoText: TextView = view.findViewById(R.id.estadoText)

        aulaText.text = "Aula: ${incidencia.aula_nombre ?: "Sin nombre"}"
        descripcionText.text = incidencia.descripcion ?: "Sin descripción"
        estadoText.text = "Estado: ${incidencia.estado ?: "pendiente"}"

        return view
    }
}