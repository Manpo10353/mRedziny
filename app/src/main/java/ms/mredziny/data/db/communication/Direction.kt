package ms.mredziny.data.db.communication

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "direction")
data class Direction(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val directionName: String
)