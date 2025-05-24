package calculadoraGastos.Acme.controller

import android.content.Context
import calculadoraGastos.Acme.database.AppDatabase
import calculadoraGastos.Acme.model.Despesa
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RegistrarDespesaController(context: Context) {

    private val db = AppDatabase.getDatabase(context)
    private val despesaDao = db.despesaDao()

    fun registrarDespesa(nome: String, valor: Double, categoria: String, data: String, onComplete: () -> Unit) {
        GlobalScope.launch(Dispatchers.IO) {
            val novaDespesa = Despesa(
                nome = nome,
                valor = valor,
                categoria = categoria,
                data = data
            )
            despesaDao.inserirDespesa(novaDespesa)
            launch(Dispatchers.Main) {
                onComplete()
            }
        }
    }
}