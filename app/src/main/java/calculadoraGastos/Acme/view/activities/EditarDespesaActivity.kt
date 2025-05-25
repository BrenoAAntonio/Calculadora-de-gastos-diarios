package calculadoraGastos.Acme.view.activities

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import calculadoraGastos.Acme.R
import calculadoraGastos.Acme.controller.ListaDespesasController
import calculadoraGastos.Acme.controller.TagController
import calculadoraGastos.Acme.database.AppDatabase
import calculadoraGastos.Acme.model.Categoria
import calculadoraGastos.Acme.model.Despesa
import calculadoraGastos.Acme.model.DespesaTag
import calculadoraGastos.Acme.model.Tag
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditarDespesaActivity : AppCompatActivity() {

    private lateinit var controller: ListaDespesasController
    private lateinit var tagController: TagController
    private lateinit var editTextNome: TextInputEditText
    private lateinit var editTextValor: TextInputEditText
    private lateinit var spinnerCategoria: Spinner
    private lateinit var chipGroupTags: ChipGroup
    private lateinit var btnSalvar: Button
    private var despesaId: Int = -1
    private var despesaAtual: Despesa? = null
    private var listaCategorias: List<Categoria> = emptyList()
    private var listaTodasTags: List<Tag> = emptyList()
    private val despesaTagDao by lazy { AppDatabase.getDatabase(this).despesaTagDao() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_despesa)

        controller = ListaDespesasController(this)
        tagController = TagController(this)

        editTextNome = findViewById(R.id.edtNomeDespesa)
        editTextValor = findViewById(R.id.edtValorDespesa)
        spinnerCategoria = findViewById(R.id.spinnerCategoria)
        chipGroupTags = findViewById(R.id.chipGroupTags)
        btnSalvar = findViewById(R.id.btnSalvarEdicao)

        val cardHeader = findViewById<androidx.cardview.widget.CardView>(R.id.cardHeader)
        cardHeader.findViewById<android.widget.TextView>(R.id.tvAppTitle)?.text = "Editar Despesa"

        btnSalvar.text = "Salvar Edição"

        lifecycleScope.launch {
            carregarCategorias()
            tagController.allTags.observe(this@EditarDespesaActivity) { tags ->
                listaTodasTags = tags
                if (despesaId != -1) {
                    carregarDespesaParaEdicao(despesaId)
                }
            }

            despesaId = intent.getIntExtra("despesa_id", -1)
            if (despesaId != -1 && listaTodasTags.isNotEmpty()) {
                carregarDespesaParaEdicao(despesaId)
            } else if (despesaId == -1) {
                Toast.makeText(this@EditarDespesaActivity, "Erro ao carregar despesa", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        btnSalvar.setOnClickListener {
            despesaAtual?.let { despesa ->
                val nome = editTextNome.text.toString()
                val valorStr = editTextValor.text.toString()
                val categoriaSelecionada = spinnerCategoria.selectedItem.toString()
                val selectedTagIds = getSelectedTagIds()

                if (nome.isNotEmpty() && valorStr.isNotEmpty() && categoriaSelecionada.isNotEmpty()) {
                    val valor = valorStr.toDoubleOrNull()
                    if (valor != null) {
                        val despesaAtualizada = despesa.copy(
                            nome = nome,
                            valor = valor,
                            categoria = categoriaSelecionada,
                            data = despesa.data
                        )
                        lifecycleScope.launch(Dispatchers.IO) {
                            controller.atualizarDespesa(despesaAtualizada) {
                                lifecycleScope.launch(Dispatchers.IO) {
                                    despesaTagDao.getTagsForDespesa(despesaId).collectLatest { existingRelations ->
                                        existingRelations.forEach { relation ->
                                            if (!selectedTagIds.contains(relation.tagId)) {
                                                despesaTagDao.deleteRelation(despesaId, relation.tagId)
                                            }
                                        }
                                        selectedTagIds.forEach { tagId ->
                                            val alreadyExists = existingRelations.any { it.tagId == tagId }
                                            if (!alreadyExists) {
                                                despesaTagDao.insert(DespesaTag(despesaId, tagId))
                                            }
                                        }
                                        withContext(Dispatchers.Main) {
                                            Toast.makeText(this@EditarDespesaActivity, "Despesa atualizada", Toast.LENGTH_SHORT).show()
                                            finish()
                                        }
                                    }
                                }
                            }
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

    private fun carregarDespesaParaEdicao(id: Int) {
        Log.d("EditarDespesa", "Iniciando carregarDespesaParaEdicao com ID: $id")
        lifecycleScope.launch(Dispatchers.IO) {
            val despesa = controller.buscarDespesaPorId(id)
            withContext(Dispatchers.Main) {
                despesa?.let {
                    despesaAtual = it
                    editTextNome.setText(it.nome)
                    editTextValor.setText(it.valor.toString())

                    val indexCategoria = listaCategorias.indexOfFirst { categoria -> categoria.nome == it.categoria }
                    if (indexCategoria != -1) {
                        spinnerCategoria.setSelection(indexCategoria)
                    }

                    carregarTagsDaDespesa(id)
                } ?: run {
                    Toast.makeText(this@EditarDespesaActivity, "Despesa não encontrada", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }

    private fun carregarTagsDaDespesa(despesaId: Int) {
        despesaTagDao.getTagsForDespesa(despesaId).asLiveData().observe(this) { relations ->
            val tagsDaDespesa = mutableListOf<Tag>()
            lifecycleScope.launch {
                relations.forEach { relation ->
                    tagController.getTagById(relation.tagId)?.let { tag ->
                        tagsDaDespesa.add(tag)
                    }
                }
                withContext(Dispatchers.Main) {
                    exibirTags(listaTodasTags, tagsDaDespesa)
                }
            }
        }
    }

    private fun exibirTags(todasTags: List<Tag>, tagsDaDespesa: List<Tag>) {
        chipGroupTags.removeAllViews()
        Log.d("EditarDespesa", "Exibindo tags. Todas as tags: ${todasTags.size}, Tags da despesa: ${tagsDaDespesa.size}")
        for (tag in todasTags) {
            val chip = Chip(this)
            chip.text = tag.nome
            chip.isCheckable = true
            val isChecked = tagsDaDespesa.any { it.id == tag.id }
            chip.isChecked = isChecked
            Log.d("EditarDespesa", "Chip '${tag.nome}' (ID: ${tag.id}) - isChecked: $isChecked")
            chipGroupTags.addView(chip)
        }
    }

    private fun getSelectedTagIds(): List<Int> {
        val selectedIds = mutableListOf<Int>()
        for (i in 0 until chipGroupTags.childCount) {
            val chip = chipGroupTags.getChildAt(i) as Chip
            if (chip.isChecked) {
                listaTodasTags.find { it.nome == chip.text.toString() }?.let {
                    selectedIds.add(it.id)
                }
            }
        }
        return selectedIds
    }
}