package calculadoraGastos.Acme.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import calculadoraGastos.Acme.R
import calculadoraGastos.Acme.view.activities.OrcamentoComGastos
import java.text.NumberFormat
import java.util.*

class RelatorioOrcamentoAdapter(private var listaRelatorio: List<OrcamentoComGastos>) :
    RecyclerView.Adapter<RelatorioOrcamentoAdapter.RelatorioViewHolder>() {

    inner class RelatorioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCategoria: TextView = itemView.findViewById(R.id.tvCategoriaRelatorio)
        val tvOrcamento: TextView = itemView.findViewById(R.id.tvOrcamentoRelatorio)
        val tvGasto: TextView = itemView.findViewById(R.id.tvGastoRelatorio)
        val tvStatus: TextView = itemView.findViewById(R.id.tvStatusRelatorio)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RelatorioViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_relatorio_orcamento, parent, false)
        return RelatorioViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RelatorioViewHolder, position: Int) {
        val item = listaRelatorio[position]
        holder.tvCategoria.text = item.orcamento.categoria
        holder.tvOrcamento.text = formatarMoeda(item.orcamento.valorOrcamento)
        holder.tvGasto.text = formatarMoeda(item.totalGasto)

        val statusText = if (item.totalGasto <= item.orcamento.valorOrcamento) {
            "Dentro do Orçamento"
        } else {
            "Acima do Orçamento"
        }
        holder.tvStatus.text = statusText
    }

    override fun getItemCount() = listaRelatorio.size

    fun atualizarLista(novaLista: List<OrcamentoComGastos>) {
        listaRelatorio = novaLista
        notifyDataSetChanged()
    }

    private fun formatarMoeda(valor: Double): String {
        val formatoBrasil = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
        return formatoBrasil.format(valor)
    }
}