package ms.mredziny.data.db.Communication

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Direction(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val directionName: String
)