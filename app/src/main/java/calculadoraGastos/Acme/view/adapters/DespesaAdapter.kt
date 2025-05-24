package calculadoraGastos.Acme.view.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import calculadoraGastos.Acme.R
import calculadoraGastos.Acme.controller.MainController
import calculadoraGastos.Acme.model.Despesa
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DespesaAdapter(
    private var listaDespesas: List<Despesa>,
    private val onDespesaClick: (Despesa) -> Unit,
    private val onDespesaDelete: (Despesa) -> Unit = {},
    private val onDespesaEdit: (Despesa) -> Unit = {},
    private val mostrarBotaoExcluir: Boolean = true,
    private val mostrarBotaoEditar: Boolean = true,
    private val context: Context
) : RecyclerView.Adapter<DespesaAdapter.DespesaViewHolder>() {

    private val mainController = MainController(context)

    inner class DespesaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtNome: TextView = itemView.findViewById(R.id.txtNome)
        val txtValor: TextView = itemView.findViewById(R.id.txtValor)
        val txtCategoria: TextView = itemView.findViewById(R.id.txtCategoria)
        val cardIcone: CardView = itemView.findViewById(R.id.cardIcone)
        val txtIcone: TextView = itemView.findViewById(R.id.txtIcone)
        val btnRemover: ImageButton = itemView.findViewById(R.id.btnRemover)
        val btnEditar: ImageButton = itemView.findViewById(R.id.btnEditar)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onDespesaClick(listaDespesas[position])
                }
            }
            btnEditar.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onDespesaEdit(listaDespesas[position])
                }
            }
            btnRemover.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onDespesaDelete(listaDespesas[position])
                }
            }
        }
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

            CoroutineScope(Dispatchers.Main).launch {
                val color = mainController.obterCorCategoria(despesa.categoria)
                cardIcone.setCardBackgroundColor(color)
            }

            btnRemover.visibility = if (mostrarBotaoExcluir) View.VISIBLE else View.GONE
            btnEditar.visibility = if (mostrarBotaoEditar) View.VISIBLE else View.GONE
        }
    }

    override fun getItemCount() = listaDespesas.size

    fun atualizarLista(novaLista: List<Despesa>) {
        listaDespesas = novaLista
        notifyDataSetChanged()
    }
}