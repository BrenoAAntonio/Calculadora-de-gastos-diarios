package calculadoraGastos.Acme.controller

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import calculadoraGastos.Acme.database.AppDatabase
import calculadoraGastos.Acme.model.Categoria
import calculadoraGastos.Acme.model.Despesa
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ListaDespesasController(context: Context) {

    private val db = AppDatabase.getDatabase(context)
    private val despesaDao = db.despesaDao()
    private val categoriaDao = db.categoriaDao()

    private val _despesas = MutableLiveData<List<Despesa>>()
    val despesas: LiveData<List<Despesa>> get() = _despesas

    fun carregarDespesas() {
        CoroutineScope(Dispatchers.IO).launch {
            val listaDeDespesas = despesaDao.buscarTodasDespesas()
            withContext(Dispatchers.Main) {
                _despesas.value = listaDeDespesas
            }
        }
    }

    fun deletarDespesa(despesa: Despesa, onComplete: () -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            despesaDao.deletarDespesa(despesa)
            withContext(Dispatchers.Main) {
                onComplete()
                carregarDespesas()
            }
        }
    }

    suspend fun buscarDespesaPorId(id: Int): Despesa? {
        return withContext(Dispatchers.IO) {
            despesaDao.buscarDespesaPorId(id)
        }
    }

    fun atualizarDespesa(despesa: Despesa, onComplete: () -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            despesaDao.atualizarDespesa(despesa)
            withContext(Dispatchers.Main) {
                onComplete()
            }
        }
    }

    suspend fun buscarTodasCategorias(): List<Categoria> {
        return withContext(Dispatchers.IO) {
            categoriaDao.buscarTodasCategorias()
        }
    }
}