package calculadoraGastos.Acme.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "despesas")
data class Despesa(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nome: String,
    val valor: Double,
    val categoria: String,
    val data: String
)

data class CategoriaTotal(
    val categoria: String,
    val total: Double
)
