package calculadoraGastos.Acme

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import calculadoraGastos.Acme.data.AppDatabase
import calculadoraGastos.Acme.data.Despesa
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RegistrarDespesaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar_despesa)

        val edtNomeDespesa = findViewById<TextInputEditText>(R.id.edtNomeDespesa)
        val edtValorDespesa = findViewById<TextInputEditText>(R.id.edtValorDespesa)
        val spinnerCategoria = findViewById<Spinner>(R.id.spinnerCategoria)
        val btnRegistrarDespesa = findViewById<Button>(R.id.btnRegistrarDespesa)

        val categorias = arrayOf("Alimentação", "Transporte", "Lazer", "Saúde", "Outros")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categorias)
        spinnerCategoria.adapter = adapter

        val db = AppDatabase.getDatabase(this)
        val despesaDao = db.despesaDao()

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNav.selectedItemId = R.id.menu_calculator

        bottomNav.setOnNavigationItemSelectedListener { item ->
            if (item.itemId != bottomNav.selectedItemId) {
                when (item.itemId) {
                    R.id.menu_home -> {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                    R.id.menu_list -> {
                        startActivity(Intent(this, ListaDespesasActivity::class.java))
                        finish()
                    }
                }
            }
            true
        }

        btnRegistrarDespesa.setOnClickListener {
            val nome = edtNomeDespesa.text.toString().trim()
            val valorTexto = edtValorDespesa.text.toString().trim()
            val categoria = spinnerCategoria.selectedItem.toString()

            if (nome.isNotEmpty() && valorTexto.isNotEmpty()) {
                try {
                    val valor = valorTexto.toDouble()
                    val dataAtual = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

                    val novaDespesa = Despesa(
                        nome = nome,
                        valor = valor,
                        categoria = categoria,
                        data = dataAtual
                    )

                    lifecycleScope.launch(Dispatchers.IO) {
                        despesaDao.inserirDespesa(novaDespesa)
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@RegistrarDespesaActivity,
                                "Despesa salva com sucesso!",
                                Toast.LENGTH_LONG
                            ).show()
                            edtNomeDespesa.text?.clear()
                            edtValorDespesa.text?.clear()
                        }
                    }
                } catch (e: NumberFormatException) {
                    Toast.makeText(this, "Valor inválido", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            }
        }
    }
}