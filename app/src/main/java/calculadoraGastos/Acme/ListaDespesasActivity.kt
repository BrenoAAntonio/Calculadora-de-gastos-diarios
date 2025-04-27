package calculadoraGastos.Acme

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import calculadoraGastos.Acme.adapter.DespesaAdapter
import calculadoraGastos.Acme.data.AppDatabase
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ListaDespesasActivity : AppCompatActivity() {

    private lateinit var adapter: DespesaAdapter
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_despesas)

        db = AppDatabase.getDatabase(this)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewDespesas)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = DespesaAdapter(
            emptyList(),
            onDespesaClick = { despesa ->},
            onDespesaDelete = { despesa ->
                GlobalScope.launch(Dispatchers.IO) {
                    db.despesaDao().deletarDespesa(despesa)
                    runOnUiThread {
                        Toast.makeText(
                            this@ListaDespesasActivity,
                            "Despesa removida",
                            Toast.LENGTH_SHORT
                        ).show()
                        carregarDespesas()
                    }
                }
            }
        )
        recyclerView.adapter = adapter

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNav.selectedItemId = R.id.menu_list

        bottomNav.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                    true
                }
                R.id.menu_list -> {
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

        findViewById<FloatingActionButton>(R.id.fabAddDespesa).setOnClickListener {
            startActivity(Intent(this, RegistrarDespesaActivity::class.java))
        }

        carregarDespesas()
    }

    private fun carregarDespesas() {
        GlobalScope.launch(Dispatchers.IO) {
            val despesas = db.despesaDao().buscarTodasDespesas()
            runOnUiThread {
                adapter.atualizarLista(despesas)
            }
        }
    }
}
