package calculadoraGastos.Acme

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import calculadoraGastos.Acme.data.AppDatabase
import calculadoraGastos.Acme.data.Despesa
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.*
import android.content.Intent

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

        btnRegistrarDespesa.setOnClickListener {
            val nome = edtNomeDespesa.text.toString().trim()
            val valorTexto = edtValorDespesa.text.toString().trim()
            val categoria = spinnerCategoria.selectedItem.toString()

            if (nome.isNotEmpty() && valorTexto.isNotEmpty()) {
                val valor = valorTexto.toDouble()
                val dataAtual = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

                val novaDespesa = Despesa(nome = nome, valor = valor, categoria = categoria, data = dataAtual)

                despesaDao.inserirDespesa(novaDespesa)

                Toast.makeText(this, "Despesa salva com sucesso!", Toast.LENGTH_LONG).show()

                edtNomeDespesa.text?.clear()
                edtValorDespesa.text?.clear()
            } else {
                Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show()
            }
        }

        val btnVoltarMenu: Button = findViewById(R.id.btnVoltarMenu)
        btnVoltarMenu.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
