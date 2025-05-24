package calculadoraGastos.Acme.controller

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import calculadoraGastos.Acme.database.AppDatabase
import calculadoraGastos.Acme.model.Despesa
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ListaDespesasController(private val context: Context) {

    private val db = AppDatabase.getDatabase(context)
    private val _despesas = MutableLiveData<List<Despesa>>()
    val despesas: LiveData<List<Despesa>> = _despesas

    fun carregarDespesas() {
        GlobalScope.launch(Dispatchers.IO) {
            val listaDeDespesas = db.despesaDao().buscarTodasDespesas()
            _despesas.postValue(listaDeDespesas)
        }
    }

    fun deletarDespesa(despesa: Despesa, onComplete: () -> Unit) {
        GlobalScope.launch(Dispatchers.IO) {
            db.despesaDao().deletarDespesa(despesa)
            launch(Dispatchers.Main) {
                onComplete()
                carregarDespesas()
            }
        }
    }

    fun buscarDespesa(id: Int, onResult: (Despesa?) -> Unit) {
        GlobalScope.launch(Dispatchers.IO) {
            val despesa = db.despesaDao().buscarDespesaPorId(id)
            launch(Dispatchers.Main) {
                onResult(despesa)
            }
        }
    }

    fun atualizarDespesa(despesa: Despesa, onComplete: () -> Unit) {
        GlobalScope.launch(Dispatchers.IO) {
            db.despesaDao().atualizarDespesa(despesa)
            launch(Dispatchers.Main) {
                onComplete()
                carregarDespesas()
            }
        }
    }
}