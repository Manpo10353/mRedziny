package ms.mredziny.data.db.communication

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "direction")
data class DirectionEntity(
    @PrimaryKey val id: Long,
    val name: String,
)
