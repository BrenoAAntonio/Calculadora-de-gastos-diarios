package calculadoraGastos.Acme.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import calculadoraGastos.Acme.model.*

@Database(entities = [Despesa::class, Categoria::class, Tag::class, DespesaTag::class, Orcamento::class], version = 5, exportSchema = false) // Incrementamos a versão
abstract class AppDatabase : RoomDatabase() {
    abstract fun despesaDao(): DespesaDao
    abstract fun categoriaDao(): CategoriaDao
    abstract fun tagDao(): TagDao
    abstract fun despesaTagDao(): DespesaTagDao
    abstract fun orcamentoDao(): OrcamentoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "despesas_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}