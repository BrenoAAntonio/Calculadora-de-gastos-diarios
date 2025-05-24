package calculadoraGastos.Acme.view.activities

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import calculadoraGastos.Acme.R
import calculadoraGastos.Acme.controller.ListaDespesasController
import calculadoraGastos.Acme.model.Categoria
import calculadoraGastos.Acme.model.Despesa
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditarDespesaActivity : AppCompatActivity() {

    private lateinit var controller: ListaDespesasController
    private lateinit var editTextNome: TextInputEditText
    private lateinit var editTextValor: TextInputEditText
    private lateinit var spinnerCategoria: Spinner
    private lateinit var btnSalvar: Button
    private var despesaId: Int = -1
    private var despesaAtual: Despesa? = null
    private var listaCategorias: List<Categoria> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_despesa)

        controller = ListaDespesasController(this)

        editTextNome = findViewById(R.id.edtNomeDespesa)
        editTextValor = findViewById(R.id.edtValorDespesa)
        spinnerCategoria = findViewById(R.id.spinnerCategoria)
        btnSalvar = findViewById(R.id.btnRegistrarDespesa)

        val cardHeader = findViewById<androidx.cardview.widget.CardView>(R.id.cardHeader)
        cardHeader.findViewById<android.widget.TextView>(R.id.tvAppTitle)?.text = "Editar Despesa"

        btnSalvar.text = "Salvar Edição"

        CoroutineScope(Dispatchers.Main).launch {
            carregarCategorias()
            despesaId = intent.getIntExtra("despesa_id", -1)
            if (despesaId != -1) {
                carregarDespesaParaEdicao(despesaId)
            } else {
                Toast.makeText(this@EditarDespesaActivity, "Erro ao carregar despesa", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        btnSalvar.setOnClickListener {
            despesaAtual?.let { despesa ->
                val nome = editTextNome.text.toString()
                val valorStr = editTextValor.text.toString()
                val categoriaSelecionada = spinnerCategoria.selectedItem.toString()

                if (nome.isNotEmpty() && valorStr.isNotEmpty() && categoriaSelecionada.isNotEmpty()) {
                    val valor = valorStr.toDoubleOrNull()
                    if (valor != null) {
                        val despesaAtualizada = despesa.copy(
                            nome = nome,
                            valor = valor,
                            categoria = categoriaSelecionada,
                            data = despesa.data
                        )
                        controller.atualizarDespesa(despesaAtualizada) {
                            Toast.makeText(this, "Despesa atualizada", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    } else {
                        Toast.makeText(this, "Valor inválido", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private suspend fun carregarCategorias() {
        listaCategorias = controller.buscarTodasCategorias()
        withContext(Dispatchers.Main) {
            val nomesDasCategorias = listaCategorias.map { it.nome }
            val adapter = ArrayAdapter(
                this@EditarDespesaActivity,
                android.R.layout.simple_spinner_dropdown_item,
                nomesDasCategorias
            )
            spinnerCategoria.adapter = adapter
        }
    }

    private suspend fun carregarDespesaParaEdicao(id: Int) {
        val despesa = controller.buscarDespesaPorId(id)
        withContext(Dispatchers.Main) {
            despesa?.let {
                despesaAtual = it
                editTextNome.setText(it.nome)
                editTextValor.setText(it.valor.toString())

                val index = listaCategorias.indexOfFirst { categoria -> categoria.nome == it.categoria }
                if (index != -1) {
                    spinnerCategoria.setSelection(index)
                }
            } ?: run {
                Toast.makeText(this@EditarDespesaActivity, "Despesa não encontrada", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}