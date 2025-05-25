package calculadoraGastos.Acme.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orcamentos")
data class Orcamento(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val categoria: String,
    val mes: Int,
    val ano: Int,
    val valorOrcamento: Double
)