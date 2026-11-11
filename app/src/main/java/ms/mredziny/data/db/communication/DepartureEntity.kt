package ms.mredziny.data.db.communication

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "departure",
    indices = [
        Index(value = ["busStopId", "dayType", "departureTime"]),
        Index(value = ["busLineId"]),
    ],
)
data class DepartureEntity(
    @PrimaryKey val id: Long,
    val busStopId: Long,
    val busLineId: Long,
    val dayType: String,
    val departureTime: String,
)