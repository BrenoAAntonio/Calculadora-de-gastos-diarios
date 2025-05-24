package calculadoraGastos.Acme.view.activities

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import calculadoraGastos.Acme.R
import calculadoraGastos.Acme.controller.ListaDespesasController
import calculadoraGastos.Acme.model.Despesa
import com.google.android.material.textfield.TextInputEditText

class EditarDespesaActivity : AppCompatActivity() {

    private lateinit var controller: ListaDespesasController
    private lateinit var editTextNome: TextInputEditText
    private lateinit var editTextValor: TextInputEditText
    private lateinit var spinnerCategoria: Spinner
    private lateinit var btnSalvar: Button
    private var despesaId: Int = -1
    private var despesaAtual: Despesa? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_despesa)

        controller = ListaDespesasController(this)

        editTextNome = findViewById(R.id.edtNomeDespesa)
        editTextValor = findViewById(R.id.edtValorDespesa)
        spinnerCategoria = findViewById(R.id.spinnerCategoria)
        btnSalvar = findViewById(R.id.btnRegistrarDespesa)

        // Encontra o CardView do cabeçalho
        val cardHeader = findViewById<androidx.cardview.widget.CardView>(R.id.cardHeader)
        // Dentro do CardView, encontra e define o texto do título
        cardHeader.findViewById<android.widget.TextView>(R.id.tvAppTitle)?.text = "Editar Despesa"

        btnSalvar.text = "Salvar Edição"

        // Carregar as categorias no Spinner
        val categorias = resources.getStringArray(R.array.categorias_array)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categorias)
        spinnerCategoria.adapter = adapter

        despesaId = intent.getIntExtra("despesa_id", -1)

        if (despesaId != -1) {
            controller.buscarDespesa(despesaId) { despesa ->
                despesa?.let {
                    despesaAtual = it
                    editTextNome.setText(it.nome)
                    editTextValor.setText(it.valor.toString())

                    val index = categorias.indexOf(it.categoria)
                    if (index != -1) {
                        spinnerCategoria.setSelection(index)
                    }
                } ?: run {
                    Toast.makeText(this, "Despesa não encontrada", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        } else {
            Toast.makeText(this, "Erro ao carregar despesa", Toast.LENGTH_SHORT).show()
            finish()
        }

        btnSalvar.setOnClickListener {
            despesaAtual?.let { despesa ->
                val nome = editTextNome.text.toString()
                val valorStr = editTextValor.text.toString()
                val categoria = spinnerCategoria.selectedItem.toString()
                val data = despesa.data

                if (nome.isNotEmpty() && valorStr.isNotEmpty() && categoria.isNotEmpty()) {
                    val valor = valorStr.toDoubleOrNull()
                    if (valor != null) {
                        val despesaAtualizada = despesa.copy(
                            nome = nome,
                            valor = valor,
                            categoria = categoria,
                            data = data
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
}