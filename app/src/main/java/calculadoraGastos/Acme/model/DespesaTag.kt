package calculadoraGastos.Acme.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "despesa_tag",
    primaryKeys = ["despesaId", "tagId"],
    foreignKeys = [
        ForeignKey(
            entity = Despesa::class,
            parentColumns = ["id"],
            childColumns = ["despesaId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Tag::class,
            parentColumns = ["id"],
            childColumns = ["tagId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("tagId")
    ]
)
data class DespesaTag(
    val despesaId: Int,
    val tagId: Int
)