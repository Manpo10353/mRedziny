package ms.mredziny.data.db.Communication

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daytype")
data class DayType(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String
)