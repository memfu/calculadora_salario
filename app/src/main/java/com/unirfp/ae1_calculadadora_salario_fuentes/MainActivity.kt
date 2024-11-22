package com.unirfp.ae1_calculadadora_salario_fuentes

import android.content.Intent
import android.icu.text.DecimalFormat
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.slider.RangeSlider
import com.unirfp.ae1_calculadadora_salario_fuentes.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding


    // Creamos variables privadas para recoger los elementos visuales de cada componente.
    // Lo hacemos con inicialización tardi2a (lazy/perezosa) -> lateinit


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)





        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Para iniciar los componentes visuales
        initComponents()

        // Para iniciar los listeners de los eventos
        initListeners()


        // Configuraciones visuales de los componentes
        initUI()

        // Configurar View Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Lista de tipos de contratos con texto inicial
        val contractTypes = listOf("Tipo de contrato", "Indefinido", "Temporal", "Fijo-discontinuo", "Formativo")

        // Crear un adaptador para el Spinner
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item, // Layout para cada elemento
            contractTypes
        )

        // Estilo del Dropdown
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Asignar el adaptador al Spinner
        binding.spTipoContrato.adapter = adapter

        // Listener para manejar la selección de un elemento
        binding.spTipoContrato.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position == 0) {
                    // Ignorar la opción inicial
                    return
                }

                // Mostrar la opción seleccionada
                val selectedContract = contractTypes[position]
                Toast.makeText(this@MainActivity, "Seleccionaste: $selectedContract", Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // No hacer nada si no se selecciona nada
            }
        }
    }

    private fun initListeners() {
        TODO("Not yet implemented")
    }

    private fun initUI() {
        TODO("Not yet implemented")
    }

    private fun initComponents() {
        TODO("Not yet implemented")
    }


}