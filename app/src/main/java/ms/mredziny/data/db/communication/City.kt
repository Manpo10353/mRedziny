package ms.mredziny.data.db.communication

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "city")
data class City(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val cityName: String
)
