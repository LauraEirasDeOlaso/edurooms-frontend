package com.edurooms.app.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.edurooms.app.R

class DetalleAulaActivity : AppCompatActivity() {

    private lateinit var nombreText: TextView
    private lateinit var capacidadText: TextView
    private lateinit var ubicacionText: TextView
    private lateinit var estadoText: TextView
    private lateinit var reservarButton: Button
    private lateinit var incidenciaButton: Button

    private var aulaId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_aula)

        // Obtener datos del Intent
        aulaId = intent.getIntExtra("aula_id", 0)
        val nombre = intent.getStringExtra("aula_nombre") ?: ""
        val capacidad = intent.getIntExtra("aula_capacidad", 0)
        val ubicacion = intent.getStringExtra("aula_ubicacion") ?: ""
        val estado = intent.getStringExtra("aula_estado") ?: ""

        // Vincular vistas
        nombreText = findViewById(R.id.nombreText)
        capacidadText = findViewById(R.id.capacidadText)
        ubicacionText = findViewById(R.id.ubicacionText)
        estadoText = findViewById(R.id.estadoText)
        reservarButton = findViewById(R.id.reservarButton)
        incidenciaButton = findViewById(R.id.incidenciaButton)

        // Mostrar datos
        nombreText.text = nombre
        capacidadText.text = "Capacidad: $capacidad personas"
        ubicacionText.text = "Ubicación: $ubicacion"
        estadoText.text = "Estado: $estado"

        // Click listeners
        reservarButton.setOnClickListener {
            irAReserva()
        }
        incidenciaButton.setOnClickListener {
            startActivity(Intent(this, IncidenciasActivity::class.java))
            //Toast.makeText(this, "Reportar incidencia - Próximamente", Toast.LENGTH_SHORT).show()
        }
    }

    private fun irAReserva() {
        val intent = Intent(this, ReservaActivity::class.java)
        intent.putExtra("aula_id", aulaId)
        startActivity(intent)
    }
}