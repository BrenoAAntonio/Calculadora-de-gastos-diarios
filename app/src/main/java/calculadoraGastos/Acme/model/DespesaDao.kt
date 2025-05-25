package calculadoraGastos.Acme.model

import androidx.room.*

@Dao
interface DespesaDao {
    @Insert
    suspend fun inserirDespesa(despesa: Despesa): Long

    @Query("SELECT * FROM despesas ORDER BY id DESC")
    fun buscarTodasDespesas(): List<Despesa>

    @Query("SELECT * FROM despesas WHERE id = :id")
    suspend fun buscarDespesaPorId(id: Int): Despesa?

    @Update
    suspend fun atualizarDespesa(despesa: Despesa)

    @Delete
    suspend fun deletarDespesa(despesa: Despesa)

    @Query("SELECT categoria, SUM(valor) as total FROM despesas GROUP BY categoria")
    fun buscarTotalPorCategoria(): List<CategoriaTotal>
}