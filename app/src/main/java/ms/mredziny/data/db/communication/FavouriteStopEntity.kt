package ms.mredziny.data.db.communication

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favourite_stop")
data class FavouriteStopEntity(
    @PrimaryKey val stopId: Long,
)