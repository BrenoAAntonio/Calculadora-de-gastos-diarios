package calculadoraGastos.Acme.view.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import calculadoraGastos.Acme.R
import calculadoraGastos.Acme.controller.MainController
import calculadoraGastos.Acme.view.adapters.DespesaAdapter
import com.github.mikephil.charting.charts.PieChart
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var controller: MainController
    private lateinit var adapter: DespesaAdapter
    private lateinit var pieChart: PieChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        controller = MainController(this)
        setupViews()
        setupListeners()
        carregarDados()
    }

    private fun setupViews() {
        pieChart = PieChart(this).apply {
            description.isEnabled = false
            setUsePercentValues(true)
            setExtraOffsets(5f, 10f, 5f, 5f)
            isDrawHoleEnabled = true
            setHoleColor(Color.WHITE)
            holeRadius = 58f
            setDrawCenterText(true)
            animateY(1400)
            setNoDataText("Sem despesas cadastradas")
        }

        findViewById<FrameLayout>(R.id.containerGrafico).addView(pieChart)

        val recyclerView = findViewById<RecyclerView>(R.id.rvDespesas)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = DespesaAdapter(
            emptyList(),
            onDespesaClick = {},
            onDespesaDelete = {},
            mostrarBotaoExcluir = false
        )
        recyclerView.adapter = adapter
    }

    private fun setupListeners() {
        findViewById<BottomNavigationView>(R.id.bottomNavigationView).setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_list -> {
                    navigateTo(ListaDespesasActivity::class.java)
                    true
                }
                R.id.menu_calculator -> {
                    navigateTo(CalculadoraActivity::class.java)
                    true
                }
                else -> false
            }
        }

        findViewById<FloatingActionButton>(R.id.fabAddDespesa).setOnClickListener {
            navigateTo(RegistrarDespesaActivity::class.java)
        }
    }

    private fun navigateTo(activity: Class<*>) {
        startActivity(Intent(this, activity))
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    private fun carregarDados() {
        CoroutineScope(Dispatchers.Main).launch {
            val (totalGastos, categoriaMap, despesasRecentes) = controller.carregarDados()

            findViewById<TextView>(R.id.tvTotalGastos).text = controller.formatarValorMonetario(totalGastos)
            findViewById<TextView>(R.id.tvTotalHoje).text = controller.formatarValorMonetario(totalGastos)

            pieChart.data = controller.criarDadosGrafico(categoriaMap)
            pieChart.invalidate()

            adapter.atualizarLista(despesasRecentes)
        }
    }

    override fun onResume() {
        super.onResume()
        findViewById<BottomNavigationView>(R.id.bottomNavigationView).selectedItemId = R.id.menu_home
        carregarDados()
    }
}