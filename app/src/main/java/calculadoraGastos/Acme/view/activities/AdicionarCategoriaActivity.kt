package calculadoraGastos.Acme.view.activities

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import calculadoraGastos.Acme.R
import calculadoraGastos.Acme.database.AppDatabase
import calculadoraGastos.Acme.model.Categoria
import calculadoraGastos.Acme.view.adapters.CategoriaAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

class AdicionarCategoriaActivity : BaseActivity() {

    private lateinit var etNomeCategoria: EditText
    private lateinit var btnSalvarCategoria: Button
    private lateinit var rvCategorias: RecyclerView
    private lateinit var categoriaAdapter: CategoriaAdapter
    private val db by lazy { AppDatabase.getDatabase(this).categoriaDao() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentLayout(R.layout.activity_adicionar_categoria)


        etNomeCategoria = findViewById(R.id.etNomeCategoria)
        btnSalvarCategoria = findViewById(R.id.btnSalvarCategoria)
        rvCategorias = findViewById(R.id.rvCategorias)
        rvCategorias.layoutManager = LinearLayoutManager(this)

        categoriaAdapter = CategoriaAdapter(emptyList()) { categoria ->
            excluirCategoria(categoria)
        }
        rvCategorias.adapter = categoriaAdapter

        carregarCategorias()

        btnSalvarCategoria.setOnClickListener {
            val nome = etNomeCategoria.text.toString().trim()
            if (nome.isNotEmpty()) {
                val cor = gerarCorAleatoriaHex()
                salvarCategoria(Categoria(nome = nome, cor = cor))
            } else {
                Toast.makeText(this, "Por favor, digite o nome da categoria.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun carregarCategorias() {
        CoroutineScope(Dispatchers.IO).launch {
            val categorias = db.buscarTodasCategorias()
            withContext(Dispatchers.Main) {
                categoriaAdapter.atualizarLista(categorias)
            }
        }
    }

    private fun salvarCategoria(categoria: Categoria) {
        CoroutineScope(Dispatchers.IO).launch {
            db.inserirCategoria(categoria)
            withContext(Dispatchers.Main) {
                Toast.makeText(this@AdicionarCategoriaActivity, "Categoria salva!", Toast.LENGTH_SHORT).show()
                etNomeCategoria.text.clear()
                carregarCategorias()
            }
        }
    }

    private fun excluirCategoria(categoria: Categoria) {
        CoroutineScope(Dispatchers.IO).launch {
            db.deletarCategoria(categoria)
            withContext(Dispatchers.Main) {
                Toast.makeText(this@AdicionarCategoriaActivity, "Categoria excluída!", Toast.LENGTH_SHORT).show()
                carregarCategorias()
            }
        }
    }

    private fun gerarCorAleatoriaHex(): String {
        val rnd = Random.Default
        return String.format("#%06X", (0xFFFFFF and rnd.nextInt()))
    }
}