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
import kotlin.random.Random

class MainController(private val context: Context) {

    private val db = AppDatabase.getDatabase(context)
    private val formatoMoeda = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    private val categoriaCoresCache = mutableMapOf<String, Int>()

    suspend fun carregarDados(): Triple<Double, Map<String, Double>, List<Despesa>> {
        return withContext(Dispatchers.IO) {
            val despesas = db.despesaDao().buscarTodasDespesas()
            val totalGastos = despesas.sumByDouble { it.valor }

            val categoriaMap = mutableMapOf<String, Double>()
            despesas.forEach { despesa ->
                categoriaMap[despesa.categoria] =
                    categoriaMap.getOrDefault(despesa.categoria, 0.0) + despesa.valor
            }

            val categorias = db.categoriaDao().buscarTodasCategorias()
            categorias.forEach { categoria ->
                categoria.cor?.let { corString ->
                    categoriaCoresCache[categoria.nome] = Color.parseColor(corString)
                }
            }

            Triple(totalGastos, categoriaMap, despesas.take(5))
        }
    }

    fun formatarValorMonetario(valor: Double): String {
        return formatoMoeda.format(valor)
    }

    fun criarDadosGrafico(categoriaMap: Map<String, Double>, coresMap: Map<String, Int>): PieData {
        val entries = ArrayList<PieEntry>()
        val cores = ArrayList<Int>()

        categoriaMap.forEach { (categoria, valor) ->
            entries.add(PieEntry(valor.toFloat(), categoria))
            cores.add(coresMap[categoria] ?: Color.GRAY)
        }

        val dataSet = PieDataSet(entries, "Categorias").apply {
            this.colors = cores
            valueTextSize = 12f
            valueTextColor = Color.BLACK
            sliceSpace = 3f
            selectionShift = 5f
        }

        return PieData(dataSet).apply {
            setValueFormatter(PercentFormatter())
        }
    }

    public suspend fun obterCorCategoria(categoriaNome: String): Int {
        return if (categoriaCoresCache.containsKey(categoriaNome)) {
            categoriaCoresCache[categoriaNome]!!
        } else {
            val categoria = withContext(Dispatchers.IO) {
                db.categoriaDao().buscarTodasCategorias().find { it.nome == categoriaNome }
            }
            categoria?.cor?.let { corString ->
                val cor = Color.parseColor(corString)
                categoriaCoresCache[categoriaNome] = cor
                return cor
            } ?: run {
                val novaCor = gerarCorAleatoria()
                withContext(Dispatchers.IO) {
                    val categoriaParaAtualizar = db.categoriaDao().buscarTodasCategorias().find { it.nome == categoriaNome }
                    categoriaParaAtualizar?.let {
                        db.categoriaDao().atualizarCategoria(it.copy(cor = String.format("#%06X", (0xFFFFFF and novaCor))))
                    }
                }
                categoriaCoresCache[categoriaNome] = novaCor
                return novaCor
            }
        }
    }

    private fun gerarCorAleatoria(): Int {
        val rnd = Random.Default
        return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
    }
}