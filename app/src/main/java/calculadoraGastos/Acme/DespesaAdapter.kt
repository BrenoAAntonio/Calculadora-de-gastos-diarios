package calculadoraGastos.Acme.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import calculadoraGastos.Acme.R
import calculadoraGastos.Acme.data.Despesa

class DespesaAdapter(
    private var listaDespesas: List<Despesa>,
    private val onDespesaClick: (Despesa) -> Unit = {},
    private val onDespesaDelete: (Despesa) -> Unit = {}
) : RecyclerView.Adapter<DespesaAdapter.DespesaViewHolder>() {

    inner class DespesaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtNome: TextView = itemView.findViewById(R.id.txtNome)
        val txtValor: TextView = itemView.findViewById(R.id.txtValor)
        val txtCategoria: TextView = itemView.findViewById(R.id.txtCategoria)
        val cardIcone: CardView = itemView.findViewById(R.id.cardIcone)
        val txtIcone: TextView = itemView.findViewById(R.id.txtIcone)
        val btnRemover: ImageButton = itemView.findViewById(R.id.btnRemover)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DespesaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_despesa, parent, false)
        return DespesaViewHolder(view)
    }

    override fun onBindViewHolder(holder: DespesaViewHolder, position: Int) {
        val despesa = listaDespesas[position]

        with(holder) {
            txtNome.text = despesa.nome
            txtValor.text = "R$ %.2f".format(despesa.valor)
            txtCategoria.text = despesa.categoria
            txtIcone.text = despesa.categoria.firstOrNull()?.uppercase() ?: "?"

            val color = when (despesa.categoria.lowercase()) {
                "alimentação" -> "#4CAF50"
                "transporte" -> "#2196F3"
                "lazer" -> "#FF9800"
                "saúde" -> "#F44336"
                else -> "#9C27B0"
            }
            cardIcone.setCardBackgroundColor(android.graphics.Color.parseColor(color))

            itemView.setOnClickListener { onDespesaClick(despesa) }
            btnRemover.setOnClickListener { onDespesaDelete(despesa) }
        }
    }

    override fun getItemCount() = listaDespesas.size

    fun atualizarLista(novaLista: List<Despesa>) {
        listaDespesas = novaLista
        notifyDataSetChanged()
    }
}