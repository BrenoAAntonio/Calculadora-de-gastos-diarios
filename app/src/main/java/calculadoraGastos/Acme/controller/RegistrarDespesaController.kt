package calculadoraGastos.Acme.controller

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import calculadoraGastos.Acme.database.AppDatabase
import calculadoraGastos.Acme.model.Despesa
import calculadoraGastos.Acme.model.DespesaTag
import calculadoraGastos.Acme.model.Tag
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegistrarDespesaController(private val context: Context) {

    private val db = AppDatabase.getDatabase(context)
    private val despesaDao = db.despesaDao()
    private val tagDao = db.tagDao()
    private val despesaTagDao = db.despesaTagDao()

    val allTags: LiveData<List<Tag>> = tagDao.getAll().asLiveData()

    suspend fun registrarDespesa(
        nome: String,
        valor: Double,
        categoria: String,
        data: String,
        selectedTagIds: List<Int>,
        onComplete: () -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val novaDespesa = Despesa(
                nome = nome,
                valor = valor,
                categoria = categoria,
                data = data
            )
            val despesaId = despesaDao.inserirDespesa(novaDespesa)

            if (despesaId > 0) {
                selectedTagIds.forEach { tagId ->
                    val despesaTag = DespesaTag(despesaId.toInt(), tagId)
                    despesaTagDao.insert(despesaTag)
                }
                withContext(Dispatchers.Main) {
                    onComplete()
                }
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Erro ao salvar despesa.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    suspend fun buscarTodasTags(): List<Tag> {
        return withContext(Dispatchers.IO) {
            tagDao.getAll().asLiveData().value ?: emptyList()
        }
    }
}