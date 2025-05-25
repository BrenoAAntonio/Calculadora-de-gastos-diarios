package calculadoraGastos.Acme.view.activities

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import calculadoraGastos.Acme.R
import calculadoraGastos.Acme.database.AppDatabase
import calculadoraGastos.Acme.model.Orcamento
import calculadoraGastos.Acme.view.adapters.RelatorioOrcamentoAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class OrcamentoComGastos(
    val orcamento: Orcamento,
    val totalGasto: Double
)

class RelatorioOrcamentoActivity : BaseActivity() {

    private lateinit var spinnerMes: Spinner
    private lateinit var editTextAno: EditText
    private lateinit var btnGerarRelatorio: Button
    private lateinit var rvRelatorio: RecyclerView
    private lateinit var adapter: RelatorioOrcamentoAdapter
    private val orcamentoDao by lazy { AppDatabase.getDatabase(this).orcamentoDao() }
    private val despesaDao by lazy { AppDatabase.getDatabase(this).despesaDao() }
    private val categoriaDao by lazy { AppDatabase.getDatabase(this).categoriaDao() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentLayout(R.layout.activity_relatorio_orcamento_content)

        spinnerMes = findViewById(R.id.spinnerMesRelatorio)
        editTextAno = findViewById(R.id.editTextAnoRelatorio)
        btnGerarRelatorio = findViewById(R.id.btnGerarRelatorio)
        rvRelatorio = findViewById(R.id.rvRelatorioOrcamento)

        rvRelatorio.layoutManager = LinearLayoutManager(this)
        adapter = RelatorioOrcamentoAdapter(emptyList())
        rvRelatorio.adapter = adapter

        carregarMeses()

        btnGerarRelatorio.setOnClickListener {
            gerarRelatorio()
        }
    }

    private fun carregarMeses() {
        val meses = arrayOf(
            "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
            "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"
        )
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            meses
        )
        spinnerMes.adapter = adapter
    }

    private fun gerarRelatorio() {
        val mesSelecionado = spinnerMes.selectedItemPosition + 1
        val anoTexto = editTextAno.text.toString().trim()

        if (anoTexto.isNotEmpty()) {
            try {
                val ano = anoTexto.toInt()
                lifecycleScope.launch {
                    val orcamentosDoMesAno = orcamentoDao.getOrcamentosPorMesAno(mesSelecionado, ano).collectLatest { orcamentos ->
                        val relatorioItens = mutableListOf<OrcamentoComGastos>()
                        for (orcamento in orcamentos) {
                            val totalGasto = withContext(Dispatchers.IO) {
                                despesaDao.buscarTodasDespesas()
                                    .filter { it.categoria == orcamento.categoria && getMesAnoFromDate(it.data) == Pair(mesSelecionado, ano) }
                                    .sumOf { it.valor }
                            }
                            relatorioItens.add(OrcamentoComGastos(orcamento, totalGasto))
                        }
                        withContext(Dispatchers.Main) {
                            adapter.atualizarLista(relatorioItens)
                        }
                    }
                }
            } catch (e: NumberFormatException) {
                Toast.makeText(this, "Ano inválido", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Informe o ano", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getMesAnoFromDate(data: String): Pair<Int, Int> {
        val parts = data.split("/")
        if (parts.size == 3) {
            return Pair(parts[1].toInt(), parts[2].toInt())
        }
        return Pair(-1, -1)
    }
}