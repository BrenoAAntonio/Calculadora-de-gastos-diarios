package calculadoraGastos.Acme.view.activities

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import calculadoraGastos.Acme.R
import calculadoraGastos.Acme.controller.RegistrarDespesaController
import calculadoraGastos.Acme.controller.TagController
import calculadoraGastos.Acme.database.AppDatabase
import calculadoraGastos.Acme.model.DespesaTag
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class RegistrarDespesaActivity : AppCompatActivity() {

    private lateinit var controller: RegistrarDespesaController
    private lateinit var tagController: TagController
    private lateinit var edtNomeDespesa: TextInputEditText
    private lateinit var edtValorDespesa: TextInputEditText
    private lateinit var spinnerCategoria: Spinner
    private lateinit var chipGroupTags: ChipGroup
    private val dbCategoria by lazy { AppDatabase.getDatabase(this).categoriaDao() }

    private var listaDeTags = listOf<calculadoraGastos.Acme.model.Tag>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar_despesa)

        controller = RegistrarDespesaController(this)
        tagController = TagController(this)
        edtNomeDespesa = findViewById(R.id.edtNomeDespesa)
        edtValorDespesa = findViewById(R.id.edtValorDespesa)
        spinnerCategoria = findViewById(R.id.spinnerCategoria)
        chipGroupTags = findViewById(R.id.chipGroupTags)
        val btnRegistrarDespesa = findViewById<Button>(R.id.btnRegistrarDespesa)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        carregarCategorias()
        carregarTags()

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
            lifecycleScope.launch {
                val nome = edtNomeDespesa.text.toString().trim()
                val valorTexto = edtValorDespesa.text.toString().trim()
                val categoria = spinnerCategoria.selectedItem.toString()
                val dataAtual = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
                val selectedTagIds = getSelectedTagIds()

                if (nome.isNotEmpty() && valorTexto.isNotEmpty()) {
                    try {
                        val valor = valorTexto.toDouble()
                        controller.registrarDespesa(nome, valor, categoria, dataAtual, selectedTagIds) {
                            launch(Dispatchers.Main) {
                                Toast.makeText(
                                    this@RegistrarDespesaActivity,
                                    "Despesa salva com sucesso!",
                                    Toast.LENGTH_LONG
                                ).show()
                                edtNomeDespesa.text?.clear()
                                edtValorDespesa.text?.clear()
                                chipGroupTags.clearCheck() // Limpa a seleção de tags
                            }
                        }
                    } catch (e: NumberFormatException) {
                        Toast.makeText(this@RegistrarDespesaActivity, "Valor inválido", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@RegistrarDespesaActivity, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun carregarCategorias() {
        lifecycleScope.launch(Dispatchers.IO) {
            val categoriasDoBanco = dbCategoria.buscarTodasCategorias()
            val nomesDasCategorias = categoriasDoBanco.map { categoria -> categoria.nome }.toTypedArray<String>()
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

    private fun carregarTags() {
        tagController.allTags.observe(this) { tags ->
            listaDeTags = tags
            exibirTags(tags)
        }
    }

    private fun exibirTags(tags: List<calculadoraGastos.Acme.model.Tag>) {
        chipGroupTags.removeAllViews()
        for (tag in tags) {
            val chip = Chip(this)
            chip.text = tag.nome
            chip.isCheckable = true
            chipGroupTags.addView(chip)
        }
    }

    private fun getSelectedTagIds(): List<Int> {
        val selectedIds = mutableListOf<Int>()
        for (i in 0 until chipGroupTags.childCount) {
            val chip = chipGroupTags.getChildAt(i) as Chip
            if (chip.isChecked) {
                val tagName = chip.text.toString()
                listaDeTags.find { it.nome == tagName }?.let {
                    selectedIds.add(it.id)
                }
            }
        }
        return selectedIds
    }
}