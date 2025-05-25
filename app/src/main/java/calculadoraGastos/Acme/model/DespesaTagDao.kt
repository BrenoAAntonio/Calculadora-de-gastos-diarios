package calculadoraGastos.Acme.model

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DespesaTagDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(despesaTag: DespesaTag): Long

    @Delete
    suspend fun delete(despesaTag: DespesaTag)

    @Query("SELECT * FROM despesa_tag WHERE despesaId = :despesaId")
    fun getTagsForDespesa(despesaId: Int): Flow<List<DespesaTag>>

    @Query("SELECT * FROM despesa_tag WHERE tagId = :tagId")
    fun getDespesasForTag(tagId: Int): Flow<List<DespesaTag>>

    @Query("DELETE FROM despesa_tag WHERE despesaId = :despesaId AND tagId = :tagId")
    suspend fun deleteRelation(despesaId: Int, tagId: Int)
}