package ms.mredziny.data.db.communication

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daytype")
data class DayType(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val type: String
)