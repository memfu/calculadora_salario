package com.unirfp.ae1_calculadora_salario_fuentes

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.unirfp.ae1_calculadora_salario_fuentes.MainActivity.Companion.Salario_KEY
import com.unirfp.ae1_calculadora_salario_fuentes.databinding.ActivityResultSalarioNetoBinding
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

class ResultSalarioNeto : AppCompatActivity() {

    // Variable necesaria para llamar a los componentes por su id
    private lateinit var binding: ActivityResultSalarioNetoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Se configura View Binding
        binding = ActivityResultSalarioNetoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        
        val resultadoSalario = intent.extras?.getDouble(Salario_KEY) ?: -1.0

        initListener()

        initUI(resultadoSalario)
        
    }

    private fun initUI(resultadoSalario: Double) {
        val salarioFormateado = formatEuro(resultadoSalario)
        binding.tvResultado.text = salarioFormateado.toString() + " euros"
    }

    private fun initListener() {
        binding.btnRecalc.setOnClickListener{
            onBackPressedDispatcher.onBackPressed() //para volver hacia atrás
        }
    }

    fun formatEuro(value: Double): String {
        val decimalFormatSymbols = DecimalFormatSymbols(Locale.getDefault()).apply {
            groupingSeparator = '.' // Separador de miles
            decimalSeparator = ','  // Separador de decimales
        }

        val decimalFormat = DecimalFormat("#,##0.00 €", decimalFormatSymbols)
        return decimalFormat.format(value)
    }
}