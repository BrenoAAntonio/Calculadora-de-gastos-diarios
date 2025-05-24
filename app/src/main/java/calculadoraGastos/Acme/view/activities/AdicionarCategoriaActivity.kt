package calculadoraGastos.Acme.view.activities

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import calculadoraGastos.Acme.R
import calculadoraGastos.Acme.database.AppDatabase
import calculadoraGastos.Acme.model.Categoria
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AdicionarCategoriaActivity : AppCompatActivity() {

    private lateinit var etNomeCategoria: EditText
    private lateinit var btnSalvarCategoria: Button
    private lateinit var btnVoltarCategoria: ImageButton
    private val db by lazy { AppDatabase.getDatabase(this).categoriaDao() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adicionar_categoria)

        etNomeCategoria = findViewById(R.id.etNomeCategoria)
        btnSalvarCategoria = findViewById(R.id.btnSalvarCategoria)
        btnVoltarCategoria = findViewById(R.id.btnVoltarCategoria)

        btnSalvarCategoria.setOnClickListener {
            val nome = etNomeCategoria.text.toString().trim()
            if (nome.isNotEmpty()) {
                salvarCategoria(Categoria(nome = nome))
            } else {
                Toast.makeText(this, "Por favor, digite o nome da categoria.", Toast.LENGTH_SHORT).show()
            }
        }

        btnVoltarCategoria.setOnClickListener {
            finish()
        }
    }

    private fun salvarCategoria(categoria: Categoria) {
        CoroutineScope(Dispatchers.IO).launch {
            db.inserirCategoria(categoria)
            runOnUiThread {
                Toast.makeText(this@AdicionarCategoriaActivity, "Categoria salva!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}