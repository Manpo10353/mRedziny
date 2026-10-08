package ms.mredziny.data.db.Communication

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "busline")
data class BusLine(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String
)