package calculadoraGastos.Acme.view.activities

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import calculadoraGastos.Acme.R
import calculadoraGastos.Acme.controller.TagController
import calculadoraGastos.Acme.model.Tag
import calculadoraGastos.Acme.view.adapters.TagAdapter

class GerenciarTagsActivity : AppCompatActivity() {

    private lateinit var tagController: TagController
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TagAdapter
    private lateinit var editTextNovaTag: EditText
    private lateinit var buttonAdicionarTag: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gerenciar_tags)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Gerenciar Tags"

        tagController = TagController(this)

        editTextNovaTag = findViewById(R.id.editTextNovaTag)
        buttonAdicionarTag = findViewById(R.id.buttonAdicionarTag)
        recyclerView = findViewById(R.id.recyclerViewTags)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = TagAdapter(
            emptyList(),
            onEditClick = { tag ->
                showEditDialog(tag)
            },
            onDeleteClick = { tag ->
                tagController.deleteTag(tag) {
                    Toast.makeText(this, "Tag '${tag.nome}' removida", Toast.LENGTH_SHORT).show()
                }
            }
        )
        recyclerView.adapter = adapter

        observeTags()

        buttonAdicionarTag.setOnClickListener {
            val nomeNovaTag = editTextNovaTag.text.toString().trim()
            if (nomeNovaTag.isNotEmpty()) {
                tagController.insertTag(nomeNovaTag) { id ->
                    if (id > 0) {
                        editTextNovaTag.text.clear()
                        Toast.makeText(this, "Tag '$nomeNovaTag' adicionada", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Erro ao adicionar tag", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Digite o nome da tag", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeTags() {
        tagController.allTags.observe(this) { tags ->
            adapter.updateList(tags)
        }
    }

    private fun showEditDialog(tag: Tag) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Editar Tag")

        val input = EditText(this)
        input.setText(tag.nome)
        builder.setView(input)

        builder.setPositiveButton("Salvar") { dialog, _ ->
            val novoNome = input.text.toString().trim()
            if (novoNome.isNotEmpty()) {
                val tagAtualizada = tag.copy(nome = novoNome)
                tagController.updateTag(tagAtualizada) {
                    Toast.makeText(this, "Tag atualizada para '$novoNome'", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "O nome da tag não pode ser vazio", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }
        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}