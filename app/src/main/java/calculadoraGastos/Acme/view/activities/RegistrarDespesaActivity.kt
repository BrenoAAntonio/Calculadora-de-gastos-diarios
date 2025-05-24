package calculadoraGastos.Acme.view.activities

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import calculadoraGastos.Acme.R
import calculadoraGastos.Acme.controller.RegistrarDespesaController
import calculadoraGastos.Acme.database.AppDatabase
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class RegistrarDespesaActivity : AppCompatActivity() {

    private lateinit var controller: RegistrarDespesaController
    private lateinit var edtNomeDespesa: TextInputEditText
    private lateinit var edtValorDespesa: TextInputEditText
    private lateinit var spinnerCategoria: Spinner
    private val db by lazy { AppDatabase.getDatabase(this).categoriaDao() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar_despesa)

        controller = RegistrarDespesaController(this)
        edtNomeDespesa = findViewById(R.id.edtNomeDespesa)
        edtValorDespesa = findViewById(R.id.edtValorDespesa)
        spinnerCategoria = findViewById(R.id.spinnerCategoria)
        val btnRegistrarDespesa = findViewById<Button>(R.id.btnRegistrarDespesa)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        carregarCategorias()

        bottomNav.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                    true
                }
                R.id.menu_list -> {
                    startActivity(Intent(this, ListaDespesasActivity::class.java))
                    finish()
                    true
                }
                R.id.menu_calculator -> {
                    startActivity(Intent(this, CalculadoraActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }

        btnRegistrarDespesa.setOnClickListener {
            val nome = edtNomeDespesa.text.toString().trim()
            val valorTexto = edtValorDespesa.text.toString().trim()
            val categoria = spinnerCategoria.selectedItem.toString()
            val dataAtual = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

            if (nome.isNotEmpty() && valorTexto.isNotEmpty()) {
                try {
                    val valor = valorTexto.toDouble()
                    controller.registrarDespesa(nome, valor, categoria, dataAtual) {
                        Toast.makeText(
                            this@RegistrarDespesaActivity,
                            "Despesa salva com sucesso!",
                            Toast.LENGTH_LONG
                        ).show()
                        edtNomeDespesa.text?.clear()
                        edtValorDespesa.text?.clear()
                    }
                } catch (e: NumberFormatException) {
                    Toast.makeText(this, "Valor inválido", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun carregarCategorias() {
        CoroutineScope(Dispatchers.IO).launch {
            val categoriasDoBanco = db.buscarTodasCategorias()
            val nomesDasCategorias = categoriasDoBanco.map { it.nome }.toTypedArray()
            withContext(Dispatchers.Main) {
                val adapter = ArrayAdapter(
                    this@RegistrarDespesaActivity,
                    android.R.layout.simple_spinner_dropdown_item,
                    nomesDasCategorias
                )
                spinnerCategoria.adapter = adapter
            }
        }
    }
}