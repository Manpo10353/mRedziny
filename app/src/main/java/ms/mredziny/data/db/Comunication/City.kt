package ms.mredziny.data.db.Comunication

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class City(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val cityName: String
)
