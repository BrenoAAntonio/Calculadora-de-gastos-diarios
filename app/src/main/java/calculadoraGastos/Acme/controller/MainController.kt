package calculadoraGastos.Acme.controller

import android.content.Context
import android.graphics.Color
import calculadoraGastos.Acme.database.AppDatabase
import calculadoraGastos.Acme.model.Despesa
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.util.*

class MainController(private val context: Context) {

    private val db = AppDatabase.getDatabase(context)
    private val formatoMoeda = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

    suspend fun carregarDados(): Triple<Double, Map<String, Double>, List<Despesa>> {
        return withContext(Dispatchers.IO) {
            val despesas = db.despesaDao().buscarTodasDespesas()
            val totalGastos = despesas.sumByDouble { it.valor }

            val categoriaMap = mutableMapOf<String, Double>()
            despesas.forEach { despesa ->
                categoriaMap[despesa.categoria] =
                    categoriaMap.getOrDefault(despesa.categoria, 0.0) + despesa.valor
            }

            Triple(totalGastos, categoriaMap, despesas.take(5))
        }
    }

    fun formatarValorMonetario(valor: Double): String {
        return formatoMoeda.format(valor)
    }

    fun criarDadosGrafico(categoriaMap: Map<String, Double>): PieData {
        val entries = ArrayList<PieEntry>()
        val cores = ArrayList<Int>()

        categoriaMap.forEach { (categoria, valor) ->
            entries.add(PieEntry(valor.toFloat(), categoria))
            cores.add(obterCorCategoria(categoria))
        }

        val dataSet = PieDataSet(entries, "Categorias").apply {
            colors = cores
            valueTextSize = 12f
            valueTextColor = Color.BLACK
            sliceSpace = 3f
            selectionShift = 5f
        }

        return PieData(dataSet).apply {
            setValueFormatter(PercentFormatter())
        }
    }

    private fun obterCorCategoria(categoria: String): Int {
        return when (categoria.lowercase()) {
            "alimentação" -> Color.parseColor("#4CAF50")
            "transporte" -> Color.parseColor("#2196F3")
            "lazer" -> Color.parseColor("#FF9800")
            "saúde" -> Color.parseColor("#F44336")
            else -> Color.parseColor("#9C27B0")
        }
    }
}