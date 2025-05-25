package calculadoraGastos.Acme.model

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface OrcamentoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(orcamento: Orcamento)

    @Update
    suspend fun update(orcamento: Orcamento)

    @Delete
    suspend fun delete(orcamento: Orcamento)

    @Query("SELECT * FROM orcamentos")
    fun getAll(): Flow<List<Orcamento>>

    @Query("SELECT * FROM orcamentos WHERE id = :id")
    suspend fun getById(id: Int): Orcamento?

    @Query("SELECT * FROM orcamentos WHERE categoria = :categoria AND mes = :mes AND ano = :ano")
    suspend fun getOrcamentoPorCategoriaMesAno(categoria: String, mes: Int, ano: Int): Orcamento?

    @Query("SELECT * FROM orcamentos WHERE mes = :mes AND ano = :ano")
    fun getOrcamentosPorMesAno(mes: Int, ano: Int): Flow<List<Orcamento>>
}