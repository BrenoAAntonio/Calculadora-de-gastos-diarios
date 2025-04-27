package calculadoraGastos.Acme

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import calculadoraGastos.Acme.adapter.DespesaAdapter
import calculadoraGastos.Acme.data.AppDatabase
import calculadoraGastos.Acme.data.Despesa
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var adapter: DespesaAdapter
    private lateinit var pieChart: PieChart
    private lateinit var tvTotalGastos: TextView
    private lateinit var tvTotalHoje: TextView
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = AppDatabase.getDatabase(this)

        tvTotalGastos = findViewById(R.id.tvTotalGastos)
        tvTotalHoje = findViewById(R.id.tvTotalHoje)

        recyclerView = findViewById(R.id.rvDespesas)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = DespesaAdapter(
            emptyList(),
            onDespesaClick = {},
            onDespesaDelete = {},
            mostrarBotaoExcluir = false
        )
        recyclerView.adapter = adapter

        setupPieChart()

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNav.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_home -> {
                    true
                }
                R.id.menu_list -> {
                    startActivity(Intent(this, ListaDespesasActivity::class.java))
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    true
                }
                R.id.menu_calculator -> {
                    startActivity(Intent(this, RegistrarDespesaActivity::class.java))
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    true
                }
                else -> false
            }
        }

        findViewById<FloatingActionButton>(R.id.fabAddDespesa).setOnClickListener {
            val intent = Intent(this, RegistrarDespesaActivity::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        carregarDados()
    }

    private fun setupPieChart() {
        val containerGrafico = findViewById<android.widget.FrameLayout>(R.id.containerGrafico)

        pieChart = PieChart(this)
        containerGrafico.addView(pieChart)

        pieChart.apply {
            description.isEnabled = false
            setUsePercentValues(true)
            setExtraOffsets(5f, 10f, 5f, 5f)
            dragDecelerationFrictionCoef = 0.95f
            isDrawHoleEnabled = true
            setHoleColor(Color.WHITE)
            holeRadius = 58f
            transparentCircleRadius = 61f
            setDrawCenterText(true)
            rotationAngle = 0f
            isRotationEnabled = true
            isHighlightPerTapEnabled = true
            animateY(1400)
            legend.isEnabled = true
            setEntryLabelColor(Color.BLACK)
            setEntryLabelTextSize(12f)
            setNoDataText("Sem despesas cadastradas")
        }
    }

    private fun carregarDados() {
        GlobalScope.launch(Dispatchers.IO) {
            val despesas = db.despesaDao().buscarTodasDespesas()

            val totalGastos = despesas.sumByDouble { it.valor }

            val categoriaMap = mutableMapOf<String, Double>()
            despesas.forEach { despesa ->
                val valorAtual = categoriaMap.getOrDefault(despesa.categoria, 0.0)
                categoriaMap[despesa.categoria] = valorAtual + despesa.valor
            }

            withContext(Dispatchers.Main) {
                val formatoMoeda = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
                tvTotalGastos.text = formatoMoeda.format(totalGastos)
                tvTotalHoje.text = formatoMoeda.format(totalGastos)

                atualizarGraficoPizza(categoriaMap)

                val despesasRecentes = despesas.take(5)
                adapter.atualizarLista(despesasRecentes)
            }
        }
    }

    private fun atualizarGraficoPizza(categoriaMap: Map<String, Double>) {
        if (categoriaMap.isEmpty()) {
            pieChart.setNoDataText("Sem despesas cadastradas")
            pieChart.invalidate()
            return
        }

        val entries = ArrayList<PieEntry>()
        val cores = ArrayList<Int>()

        categoriaMap.forEach { (categoria, valor) ->
            entries.add(PieEntry(valor.toFloat(), categoria))

            val cor = when (categoria.lowercase()) {
                "alimentação" -> Color.parseColor("#4CAF50")
                "transporte" -> Color.parseColor("#2196F3")
                "lazer" -> Color.parseColor("#FF9800")
                "saúde" -> Color.parseColor("#F44336")
                else -> Color.parseColor("#9C27B0")
            }
            cores.add(cor)
        }

        val dataSet = PieDataSet(entries, "Categorias")
        dataSet.apply {
            colors = cores
            valueTextSize = 12f
            valueTextColor = Color.BLACK
            sliceSpace = 3f
            selectionShift = 5f
        }

        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter(pieChart))

        pieChart.apply {
            this.data = data
            centerText = "Categorias"
            invalidate()
            animateY(1400)
        }
    }

    override fun onResume() {
        super.onResume()
        findViewById<BottomNavigationView>(R.id.bottomNavigationView).selectedItemId = R.id.menu_home
        carregarDados()
    }
}
