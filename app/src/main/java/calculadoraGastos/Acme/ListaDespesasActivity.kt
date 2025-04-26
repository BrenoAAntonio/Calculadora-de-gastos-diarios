package calculadoraGastos.Acme

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import calculadoraGastos.Acme.adapter.DespesaAdapter
import calculadoraGastos.Acme.data.AppDatabase
import android.content.Intent
import android.widget.Button

class ListaDespesasActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_despesas)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewDespesas)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val despesas = AppDatabase.getDatabase(this).despesaDao().buscarTodasDespesas()

        val adapter = DespesaAdapter(despesas)
        recyclerView.adapter = adapter

        val btnVoltarMenu = findViewById<Button>(R.id.btnVoltarMenu)
        btnVoltarMenu.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}