package calculadoraGastos.Acme.model

import androidx.room.*

@Dao
interface CategoriaDao {
    @Insert
    fun inserirCategoria(categoria: Categoria)

    @Query("SELECT * FROM categorias ORDER BY nome ASC")
    fun buscarTodasCategorias(): List<Categoria>

    @Query("SELECT * FROM categorias WHERE id = :id")
    fun buscarCategoriaPorId(id: Int): Categoria?

    @Update
    fun atualizarCategoria(categoria: Categoria)

    @Delete
    fun deletarCategoria(categoria: Categoria)
}