package calculadoraGastos.Acme.view.activities

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import calculadoraGastos.Acme.R
import calculadoraGastos.Acme.database.AppDatabase
import calculadoraGastos.Acme.model.Categoria
import calculadoraGastos.Acme.model.Orcamento
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DefinirOrcamentoActivity : AppCompatActivity() {

    private lateinit var spinnerCategoria: Spinner
    private lateinit var spinnerMes: Spinner
    private lateinit var editTextAno: EditText
    private lateinit var editTextValor: EditText
    private lateinit var btnSalvar: Button
    private val categoriaDao by lazy { AppDatabase.getDatabase(this).categoriaDao() }
    private val orcamentoDao by lazy { AppDatabase.getDatabase(this).orcamentoDao() }
    private var listaCategorias: List<Categoria> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_definir_orcamento)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        spinnerCategoria = findViewById(R.id.spinnerCategoriaOrcamento)
        spinnerMes = findViewById(R.id.spinnerMesOrcamento)
        editTextAno = findViewById(R.id.editTextAnoOrcamento)
        editTextValor = findViewById(R.id.editTextValorOrcamento)
        btnSalvar = findViewById(R.id.btnSalvarOrcamento)

        carregarCategorias()
        carregarMeses()

        btnSalvar.setOnClickListener {
            salvarOrcamento()
        }
    }

    private fun carregarCategorias() {
        lifecycleScope.launch(Dispatchers.IO) {
            listaCategorias = categoriaDao.buscarTodasCategorias()
            val nomesCategorias = listaCategorias.map { it.nome }.toTypedArray()
            withContext(Dispatchers.Main) {
                val adapter = ArrayAdapter(
                    this@DefinirOrcamentoActivity,
                    android.R.layout.simple_spinner_dropdown_item,
                    nomesCategorias
                )
                spinnerCategoria.adapter = adapter
            }
        }
    }

    private fun carregarMeses() {
        val meses = arrayOf(
            "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
            "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"
        )
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            meses
        )
        spinnerMes.adapter = adapter
    }

    private fun salvarOrcamento() {
        val categoriaSelecionada = spinnerCategoria.selectedItem.toString()
        val mesSelecionado = spinnerMes.selectedItemPosition + 1 // Janeiro é 0
        val anoTexto = editTextAno.text.toString().trim()
        val valorTexto = editTextValor.text.toString().trim()

        if (anoTexto.isNotEmpty() && valorTexto.isNotEmpty()) {
            try {
                val ano = anoTexto.toInt()
                val valor = valorTexto.toDouble()

                lifecycleScope.launch(Dispatchers.IO) {
                    val orcamento = Orcamento(
                        categoria = categoriaSelecionada,
                        mes = mesSelecionado,
                        ano = ano,
                        valorOrcamento = valor
                    )
                    orcamentoDao.insert(orcamento)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@DefinirOrcamentoActivity, "Orçamento salvo!", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            } catch (e: NumberFormatException) {
                Toast.makeText(this, "Ano ou valor inválido", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}