package calculadoraGastos.Acme.data

import androidx.room.*

@Dao
interface DespesaDao {
    @Insert
    fun inserirDespesa(despesa: Despesa)

    @Query("SELECT * FROM despesas ORDER BY id DESC")
    fun buscarTodasDespesas(): List<Despesa>

    @Delete
    fun deletarDespesa(despesa: Despesa)

    @Query("SELECT categoria, SUM(valor) as total FROM despesas GROUP BY categoria")
    fun buscarTotalPorCategoria(): List<CategoriaTotal>
}
