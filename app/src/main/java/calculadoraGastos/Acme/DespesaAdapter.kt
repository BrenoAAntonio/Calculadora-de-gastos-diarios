package calculadoraGastos.Acme.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import calculadoraGastos.Acme.R
import calculadoraGastos.Acme.data.Despesa

class DespesaAdapter(private val listaDespesas: List<Despesa>) :
    RecyclerView.Adapter<DespesaAdapter.DespesaViewHolder>() {

    class DespesaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtNome: TextView = itemView.findViewById(R.id.txtNome)
        val txtValor: TextView = itemView.findViewById(R.id.txtValor)
        val txtCategoria: TextView = itemView.findViewById(R.id.txtCategoria)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DespesaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_despesa, parent, false)
        return DespesaViewHolder(view)
    }

    override fun onBindViewHolder(holder: DespesaViewHolder, position: Int) {
        val despesa = listaDespesas[position]
        holder.txtNome.text = despesa.nome
        holder.txtValor.text = "R$ ${despesa.valor}"
        holder.txtCategoria.text = despesa.categoria
    }

    override fun getItemCount() = listaDespesas.size
}
