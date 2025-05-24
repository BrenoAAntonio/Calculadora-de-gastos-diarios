package calculadoraGastos.Acme.model

import androidx.room.*

@Dao
interface DespesaDao {
    @Insert
    fun inserirDespesa(despesa: Despesa)

    @Query("SELECT * FROM despesas ORDER BY id DESC")
    fun buscarTodasDespesas(): List<Despesa>

    @Query("SELECT * FROM despesas WHERE id = :id")
    fun buscarDespesaPorId(id: Int): Despesa?

    @Update
    fun atualizarDespesa(despesa: Despesa)

    @Delete
    fun deletarDespesa(despesa: Despesa)

    @Query("SELECT categoria, SUM(valor) as total FROM despesas GROUP BY categoria")
    fun buscarTotalPorCategoria(): List<CategoriaTotal>
}