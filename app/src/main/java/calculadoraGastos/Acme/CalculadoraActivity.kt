package calculadoraGastos.Acme

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import net.objecthunter.exp4j.Expression
import net.objecthunter.exp4j.ExpressionBuilder

class CalculadoraActivity : AppCompatActivity() {

    private lateinit var tvOperation: TextView
    private lateinit var tvResult: TextView
    private var lastNumeric = false
    private var stateError = false
    private var lastDot = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculadora)

        tvOperation = findViewById(R.id.tvOperation)
        tvResult = findViewById(R.id.tvResult)

        findViewById<Button>(R.id.btnZero).setOnClickListener { onDigitClick(it) }
        findViewById<Button>(R.id.btnOne).setOnClickListener { onDigitClick(it) }
        findViewById<Button>(R.id.btnTwo).setOnClickListener { onDigitClick(it) }
        findViewById<Button>(R.id.btnThree).setOnClickListener { onDigitClick(it) }
        findViewById<Button>(R.id.btnFour).setOnClickListener { onDigitClick(it) }
        findViewById<Button>(R.id.btnFive).setOnClickListener { onDigitClick(it) }
        findViewById<Button>(R.id.btnSix).setOnClickListener { onDigitClick(it) }
        findViewById<Button>(R.id.btnSeven).setOnClickListener { onDigitClick(it) }
        findViewById<Button>(R.id.btnEight).setOnClickListener { onDigitClick(it) }
        findViewById<Button>(R.id.btnNine).setOnClickListener { onDigitClick(it) }
        findViewById<Button>(R.id.btnDecimal).setOnClickListener { onDecimalPointClick(it) }

        findViewById<Button>(R.id.btnAdd).setOnClickListener { onOperatorClick(it) }
        findViewById<Button>(R.id.btnSubtract).setOnClickListener { onOperatorClick(it) }
        findViewById<Button>(R.id.btnMultiply).setOnClickListener { onOperatorClick(it) }
        findViewById<Button>(R.id.btnDivide).setOnClickListener { onOperatorClick(it) }

        findViewById<Button>(R.id.btnClear).setOnClickListener { onClearClick(it) }
        findViewById<Button>(R.id.btnEquals).setOnClickListener { onEqualsClick(it) }
        findViewById<Button>(R.id.btnParentheses).setOnClickListener { onParenthesesClick(it) }
        findViewById<Button>(R.id.btnPercent).setOnClickListener { onPercentClick(it) }
        findViewById<Button>(R.id.btnPlusMinus).setOnClickListener { onPlusMinusClick(it) }

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNav.selectedItemId = R.id.menu_calculator
        bottomNav.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_home -> {
                    finish()
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    true
                }
                R.id.menu_list -> {
                    startActivity(Intent(this, ListaDespesasActivity::class.java))
                    finish()
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    true
                }
                R.id.menu_calculator -> true
                else -> false
            }
        }
    }

    fun onDigitClick(view: View) {
        if (stateError) {
            tvOperation.text = (view as Button).text
            stateError = false
        } else {
            tvOperation.append((view as Button).text)
        }
        lastNumeric = true
    }

    fun onDecimalPointClick(view: View) {
        if (lastNumeric && !stateError && !lastDot) {
            tvOperation.append(".")
            lastNumeric = false
            lastDot = true
        }
    }

    fun onOperatorClick(view: View) {
        if (lastNumeric && !stateError) {
            tvOperation.append((view as Button).text)
            lastNumeric = false
            lastDot = false
        }
    }

    fun onClearClick(view: View) {
        tvOperation.text = ""
        tvResult.text = ""
        lastNumeric = false
        stateError = false
        lastDot = false
    }

    fun onParenthesesClick(view: View) {
        val currentText = tvOperation.text.toString()
        if (stateError) {
            tvOperation.text = "("
            stateError = false
        } else {
            val openCount = currentText.count { it == '(' }
            val closeCount = currentText.count { it == ')' }

            if (openCount <= closeCount || currentText.isEmpty() || !currentText.last().isDigit() && currentText.last() != ')') {
                tvOperation.append("(")
            } else {
                tvOperation.append(")")
            }
        }
        val updatedText = tvOperation.text.toString()
        val updatedOpenCount = updatedText.count { it == '(' }
        val updatedCloseCount = updatedText.count { it == ')' }
        lastNumeric = updatedOpenCount > updatedCloseCount
    }

    fun onEqualsClick(view: View) {
        if (lastNumeric && !stateError) {
            val txt = tvOperation.text.toString()
            try {
                val expression = ExpressionBuilder(txt
                    .replace("×", "*")
                    .replace("÷", "/"))
                    .build()
                val result = expression.evaluate()
                tvResult.text = if (result == result.toLong().toDouble()) {
                    result.toLong().toString()
                } else {
                    String.format("%.2f", result)
                }
                lastDot = true
            } catch (ex: Exception) {
                tvResult.text = "Erro"
                stateError = true
                lastNumeric = false
            }
        }
    }

    fun onPlusMinusClick(view: View) {
        if (lastNumeric && !stateError) {
            val txt = tvOperation.text.toString()
            if (txt.isNotEmpty()) {
                if (txt.startsWith("-")) {
                    tvOperation.text = txt.substring(1)
                } else {
                    tvOperation.text = "-$txt"
                }
            }
        }
    }

    fun onPercentClick(view: View) {
        if (lastNumeric && !stateError) {
            val txt = tvOperation.text.toString()
            try {
                val number = txt.toDouble()
                val result = number / 100
                tvOperation.text = result.toString()
                tvResult.text = result.toString()
                lastDot = true
            } catch (ex: Exception) {
                tvResult.text = "Erro"
                stateError = true
                lastNumeric = false
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}