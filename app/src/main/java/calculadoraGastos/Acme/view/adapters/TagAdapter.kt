package calculadoraGastos.Acme.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import calculadoraGastos.Acme.R
import calculadoraGastos.Acme.model.Tag

class TagAdapter(
    private var tagList: List<Tag>,
    private val onEditClick: (Tag) -> Unit,
    private val onDeleteClick: (Tag) -> Unit
) : RecyclerView.Adapter<TagAdapter.TagViewHolder>() {

    inner class TagViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tagNameTextView: TextView = itemView.findViewById(R.id.textViewNomeTag)
        val editButton: ImageButton = itemView.findViewById(R.id.buttonEditarTag)
        val deleteButton: ImageButton = itemView.findViewById(R.id.buttonRemoverTag)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tag, parent, false)
        return TagViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
        val currentTag = tagList[position]
        holder.tagNameTextView.text = currentTag.nome

        holder.editButton.setOnClickListener {
            onEditClick(currentTag)
        }

        holder.deleteButton.setOnClickListener {
            onDeleteClick(currentTag)
        }
    }

    override fun getItemCount() = tagList.size

    fun updateList(newList: List<Tag>) {
        tagList = newList
        notifyDataSetChanged()
    }
}