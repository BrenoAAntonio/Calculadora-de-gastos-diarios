package calculadoraGastos.Acme.model

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import android.util.Log

@Dao
interface TagDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(tag: Tag): Long

    @Update
    suspend fun update(tag: Tag)

    @Delete
    suspend fun delete(tag: Tag)

    @Query("SELECT * FROM tags ORDER BY nome ASC")
    fun getAll(): Flow<List<Tag>> {
        Log.d("TagDao", "getAll() chamado")
        return getAllInternal()
    }

    @Query("SELECT * FROM tags ORDER BY nome ASC")
    fun getAllInternal(): Flow<List<Tag>>

    @Query("SELECT * FROM tags WHERE id = :id")
    suspend fun getById(id: Int): Tag?

    @Query("SELECT * FROM tags WHERE nome = :nome")
    suspend fun getByName(nome: String): Tag?
}