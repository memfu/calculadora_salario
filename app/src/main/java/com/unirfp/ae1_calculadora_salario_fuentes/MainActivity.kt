package com.unirfp.ae1_calculadora_salario_fuentes

import android.content.Intent
import android.icu.text.DecimalFormat
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.View.OnClickListener
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.unirfp.ae1_calculadora_salario_fuentes.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity(), OnClickListener, RadioGroup.OnCheckedChangeListener {

    // Variable necesaria para llamar a los componentes por su id
    private lateinit var binding: ActivityMainBinding

    // Creación de los atributos necesarios para la lógica de nuestros componentes
    private var salarioBruto: Int? = 0
    private var salarioNeto: Double? = 0.0
    private var contratoSeleccionado: String = "Tipo de contrato"
    private var estadoSeleccionado: String = "Estado civil"
    private var comunidadSeleccionada: String = "Comunidad autónoma"

    private var nrPagas: Int = 0

    private var currentHoras: Int = 40
    private var currentChildren: Int = 0
    private var currentDiscapacidad: Int = 0


    // Creación de un companion object, que es un accesible desde todas las activities
    companion object{
        const val Salario_KEY = "Resultado_Salario"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Se configura View Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Configuraciones visuales de los componentes
        initUI()

        // Inicia los Listeners
        initListeners()

    }


    private fun initUI() {
        this.setHoras()
        this.setChildren()
        this.setDiscapacidad()
        this.setPagas()
    }

    private fun initListeners() {
        binding.btnSubHours.setOnClickListener(this)
        binding.btnPlusHours.setOnClickListener(this)
        binding.btnSubChildren.setOnClickListener(this)
        binding.btnPlusChildren.setOnClickListener(this)
        binding.btnSubDiscap.setOnClickListener(this)
        binding.btnPlusDiscap.setOnClickListener(this)
        binding.btnCalcular.setOnClickListener(this)

        this.setHoras()
        this.setChildren()
        this.setDiscapacidad()
        this.setupButtonListeners()
    }


    private fun setPagas() {
        binding.rbtn12.isChecked = true
        nrPagas = 12
    }

    private fun setHoras() {
        binding.tvHoras.text = this.currentHoras.toString()
        binding.btnSubHours.isEnabled = this.currentHoras > 0
    }

    private fun setChildren() {
        binding.tvChildren.text = this.currentChildren.toString()
        binding.btnSubChildren.isEnabled = this.currentChildren > 0
    }

    private fun setDiscapacidad() {
        binding.tvDiscapacidad.text = this.currentDiscapacidad.toString()
        binding.btnSubDiscap.isEnabled = this.currentDiscapacidad > 0
    }


    private fun calcularNeto(): Double {
        if (salarioBruto == null || salarioBruto == 0) {
            return 0.0 // Manejar caso de entrada inválida
        }

        // Ajuste del salario bruto según las horas trabajadas (salarioBruto!! para asegurar que no puede ser nulo)
        val salarioBrutoAjustado = salarioBruto!! * currentHoras / 40.0

        // Cotización a la Seguridad Social (6.35%)
        val cotSS = salarioBrutoAjustado * 0.0635

        // Base IRPF
        val baseIRPF = salarioBrutoAjustado / nrPagas

        // Determinación de la Tasa IRPF
        var tasaIRPF = 19.0 // Tasa inicial

        // Ajustes por estado civil
        val ajusteEstadoCivil = when (estadoSeleccionado) {
            "Soltero/a sin responsabilidades" -> 0.0
            "Soltero/a con responsabilidades" -> -1.0 // Responsabilidades podrían reducir un 1%
            "Casado/a con responsabilidades" -> -2.0 // Casados con responsabilidades suelen tener mayor deducción
            "Casado/a sin responsabilidades" -> -1.0 // Reducción menor si no hay dependientes
            "Divorciado/a o separado/a" -> -1.0 // Similar a soltero/a con responsabilidades, si aplica
            "Viudo/a" -> -1.5 // Viudos pueden tener reducción adicional
            else -> 0.0 // Valor por defecto para evitar errores
        }
        tasaIRPF -= ajusteEstadoCivil


        // Ajustes por número de hijos
        tasaIRPF -= currentChildren * 1.0 // Cada hijo reduce 1%

        // Ajustes por discapacidad
        tasaIRPF -= when {
            currentDiscapacidad in 33..65 -> 2.0
            currentDiscapacidad > 65 -> 3.0
            else -> 0.0
        }

        // Ajustes por Comunidad Autónoma (ejemplo simplificado)
        tasaIRPF -= when (comunidadSeleccionada) {
            "Madrid", "Canarias" -> 1.0 // Beneficio fiscal de 1% en estas comunidades
            else -> 0.0
        }

        // Asegurar que la tasa IRPF no sea negativa
        if (tasaIRPF < 0.0) tasaIRPF = 0.0

        // Retención IRPF
        val retencionIRPF = baseIRPF * (tasaIRPF / 100) * nrPagas

        // Salario Neto con formato de dos decimales
        val df = DecimalFormat("#.##")
        salarioNeto = df.format(salarioBrutoAjustado - (cotSS + retencionIRPF)).toDouble()
        return salarioNeto as Double
    }

    private fun navigateToResult(resultadoSalario: Double) {
        // Creamos la navegación entre pantallas con el object intent desde esta ventana (this) a la ResultSalarioNeto
        val intent = Intent(this,ResultSalarioNeto::class.java)

        // Agregamos el extra para pasar el result del salario neto a la pantalla: le pasamos una clave (Salario_KEY) y el valor
        // En lugar de pasarle un string directamente, creamos un objeto companion
        intent.putExtra(Salario_KEY, resultadoSalario)

        this.startActivity(intent)
    }


    override fun onClick(v: View?) {
        when(v?.id) {
            binding.btnSubHours.id -> {
                if (currentHoras > 0) {
                    this.currentHoras -= 1
                    setHoras()
                }
            }

            binding.btnPlusHours.id -> {
                this.currentHoras += 1
                setHoras()
            }

            binding.btnSubChildren.id -> {
                if (currentChildren > 0) {
                    this.currentChildren -= 1
                    setChildren()
                }
            }

            binding.btnPlusChildren.id -> {
                this.currentChildren += 1
                setChildren()
            }

            binding.btnSubDiscap.id -> {
                if (currentDiscapacidad > 0) {
                    this.currentDiscapacidad -= 1
                    setDiscapacidad()
                }
            }

            binding.btnPlusDiscap.id -> {
                if (currentDiscapacidad < 100) {
                    this.currentDiscapacidad += 1
                    setDiscapacidad()
                }
            }

            binding.btnCalcular.id -> {
                salarioBruto = binding.etSalario.text.toString().toIntOrNull() ?: 0
                if (salarioBruto != 0) {
                    contratoSeleccionado = binding.spTipoContrato.selectedItem.toString()
                    estadoSeleccionado = binding.spEstadoCivil.selectedItem.toString()
                    comunidadSeleccionada = binding.spComAuton.selectedItem.toString()
                    currentHoras = binding.tvHoras.text.toString().toIntOrNull() ?: 0
                    currentChildren = binding.tvChildren.text.toString().toIntOrNull() ?: 0
                    currentDiscapacidad = binding.tvDiscapacidad.text.toString().toIntOrNull() ?: 0
                    salarioNeto = calcularNeto()
                    // navega a la siguiente pantalla
                    navigateToResult(salarioNeto!!)
                } else {
                    binding.etSalario.error = "Por favor, ingresa un sueldo mayor que 0."
                    return
                }
            }

        }
    }

    // Para el Radiogroup de pagas
    override fun onCheckedChanged(grupoPagas: RadioGroup?, idPagaSeleccionada: Int) {
        when (grupoPagas?.id) {
            binding.radioGroupPagas.id -> {
                when (idPagaSeleccionada) {
                    binding.rbtn12.id->{
                        nrPagas = 12
                    }
                    binding.rbtn14.id->{
                        nrPagas = 14
                    }
                }
            }
        }
    }

    // Para que se pueda aumentar o reducir la cantidad manteniendo pulsado el botón - o +
    private fun setupButtonListeners() {
        val handler = android.os.Handler()

        // Función para manejar la acción de incrementar
        fun startIncrementing(action: () -> Unit): Runnable {
            return object : Runnable {
                override fun run() {
                    action()
                    handler.postDelayed(this, 150) // Repite cada 100ms
                }
            }
        }

        // Botón de incrementar horas
        binding.btnPlusHours.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    handler.post(startIncrementing {
                        currentHoras += 1
                        setHoras()
                    })
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    handler.removeCallbacksAndMessages(null) // Detén la acción
                }
            }
            true
        }

        // Botón de decrementar horas
        binding.btnSubHours.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    handler.post(startIncrementing {
                        if (currentHoras > 0) {
                            currentHoras -= 1
                            setHoras()
                        }
                    })
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    handler.removeCallbacksAndMessages(null) // Detén la acción
                }
            }
            true
        }

        // Botón de incrementar hijos
        binding.btnPlusChildren.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    handler.post(startIncrementing {
                        currentChildren += 1
                        setChildren()
                    })
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    handler.removeCallbacksAndMessages(null)
                }
            }
            true
        }

        // Botón de decrementar hijos
        binding.btnSubChildren.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    handler.post(startIncrementing {
                        if (currentChildren > 0) {
                            currentChildren -= 1
                            setChildren()
                        }
                    })
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    handler.removeCallbacksAndMessages(null)
                }
            }
            true
        }

        // Botón de incrementar discapacidad
        binding.btnPlusDiscap.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    handler.post(startIncrementing {
                        if (currentDiscapacidad < 100) {
                            currentDiscapacidad += 1
                            setDiscapacidad()
                        }
                    })
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    handler.removeCallbacksAndMessages(null)
                }
            }
            true
        }

        // Botón de decrementar discapacidad
        binding.btnSubDiscap.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    handler.post(startIncrementing {
                        if (currentDiscapacidad > 0) {
                            currentDiscapacidad -= 1
                            setDiscapacidad()
                        }
                    })
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    handler.removeCallbacksAndMessages(null)
                }
            }
            true
        }
    }

}