package calculadoraGastos.Acme

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class CalculadoraActivity : AppCompatActivity() {

    private lateinit var etResultado: EditText
    private var operador: String = ""
    private var num1: Double = 0.0
    private var num2: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculadora)

        etResultado = findViewById(R.id.etResultado)

        val btn1: Button = findViewById(R.id.btn1)
        val btn2: Button = findViewById(R.id.btn2)
        val btn3: Button = findViewById(R.id.btn3)
        val btn4: Button = findViewById(R.id.btn4)
        val btn5: Button = findViewById(R.id.btn5)
        val btn6: Button = findViewById(R.id.btn6)
        val btn7: Button = findViewById(R.id.btn7)
        val btn8: Button = findViewById(R.id.btn8)
        val btn9: Button = findViewById(R.id.btn9)
        val btn0: Button = findViewById(R.id.btn0)
        val btnSoma: Button = findViewById(R.id.btnSoma)
        val btnSubtracao: Button = findViewById(R.id.btnSubtracao)
        val btnMultiplicacao: Button = findViewById(R.id.btnMultiplicacao)
        val btnDivisao: Button = findViewById(R.id.btnDivisao)
        val btnClear: Button = findViewById(R.id.btnClear)
        val btnIgual: Button = findViewById(R.id.btnIgual)

        val buttons = listOf(btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9, btn0)

        for (button in buttons) {
            button.setOnClickListener { appendNumber(button.text.toString()) }
        }

        btnSoma.setOnClickListener { setOperator("+") }
        btnSubtracao.setOnClickListener { setOperator("-") }
        btnMultiplicacao.setOnClickListener { setOperator("*") }
        btnDivisao.setOnClickListener { setOperator("/") }
        btnClear.setOnClickListener { clear() }
        btnIgual.setOnClickListener { calculate() }
    }

    private fun appendNumber(number: String) {
        if (etResultado.text.toString() == "0") {
            etResultado.setText(number)
        } else {
            etResultado.append(number)
        }
    }

    private fun setOperator(operator: String) {
        operador = operator
        num1 = etResultado.text.toString().toDouble()
        etResultado.setText("0")
    }

    private fun clear() {
        etResultado.setText("0")
        operador = ""
        num1 = 0.0
        num2 = 0.0
    }

    private fun calculate() {
        num2 = etResultado.text.toString().toDouble()
        var result: Double = 0.0
        when (operador) {
            "+" -> result = num1 + num2
            "-" -> result = num1 - num2
            "*" -> result = num1 * num2
            "/" -> if (num2 != 0.0) result = num1 / num2 else etResultado.setText("Erro")
        }
        etResultado.setText(result.toString())
    }
}
