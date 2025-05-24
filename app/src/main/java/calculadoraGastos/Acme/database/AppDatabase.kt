package calculadoraGastos.Acme.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import calculadoraGastos.Acme.model.Categoria
import calculadoraGastos.Acme.model.CategoriaDao
import calculadoraGastos.Acme.model.Despesa
import calculadoraGastos.Acme.model.DespesaDao

@Database(entities = [Despesa::class, Categoria::class], version = 2, exportSchema = false) // Aumentamos a versão do banco
abstract class AppDatabase : RoomDatabase() {
    abstract fun despesaDao(): DespesaDao
    abstract fun categoriaDao(): CategoriaDao // Adicionamos o DAO de Categoria

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