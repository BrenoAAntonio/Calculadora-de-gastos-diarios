package calculadoraGastos.Acme.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import calculadoraGastos.Acme.R
import calculadoraGastos.Acme.model.Categoria

class CategoriaAdapter(
    private var categorias: List<Categoria>,
    private val onDeleteClick: (Categoria) -> Unit
) : RecyclerView.Adapter<CategoriaAdapter.CategoriaViewHolder>() {

    inner class CategoriaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNome: TextView = itemView.findViewById(R.id.tvNomeCategoriaItem)
        val btnExcluir: ImageButton = itemView.findViewById(R.id.btnExcluirCategoriaItem)

        fun bind(categoria: Categoria) {
            tvNome.text = categoria.nome
            btnExcluir.setOnClickListener {
                onDeleteClick(categoria)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriaViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_categoria, parent, false)
        return CategoriaViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CategoriaViewHolder, position: Int) {
        holder.bind(categorias[position])
    }

    override fun getItemCount(): Int {
        return categorias.size
    }

    fun atualizarLista(novaLista: List<Categoria>) {
        categorias = novaLista
        notifyDataSetChanged()
    }
}