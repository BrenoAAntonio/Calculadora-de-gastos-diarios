package calculadoraGastos.Acme.view.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import calculadoraGastos.Acme.R
import calculadoraGastos.Acme.view.adapters.DespesaAdapter
import calculadoraGastos.Acme.controller.ListaDespesasController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ListaDespesasActivity : AppCompatActivity() {

    private lateinit var adapter: DespesaAdapter
    private lateinit var controller: ListaDespesasController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_despesas)

        controller = ListaDespesasController(this)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewDespesas)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = DespesaAdapter(
            emptyList(),
            onDespesaClick = { despesa ->
            },
            onDespesaDelete = { despesa ->
                controller.deletarDespesa(despesa) {
                    Toast.makeText(
                        this@ListaDespesasActivity,
                        "Despesa removida",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )
        recyclerView.adapter = adapter

        controller.despesas.observe(this, Observer { despesas ->
            adapter.atualizarLista(despesas)
        })

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

        controller.carregarDespesas()
    }
}