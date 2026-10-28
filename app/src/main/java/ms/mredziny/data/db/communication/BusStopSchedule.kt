package ms.mredziny.data.db.communication

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "busStopSchedules",
    indices = [
        Index(value = ["busStopId"]),
        Index(value = ["busLineId"]),
        Index(value = ["dayTypeId"]),
        Index(value = ["scheduleId"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = BusStop::class,
            parentColumns = ["id"],
            childColumns = ["busStopId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = BusLine::class,
            parentColumns = ["id"],
            childColumns = ["busLineId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DayType::class,
            parentColumns = ["id"],
            childColumns = ["dayTypeId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Schedule::class,
            parentColumns = ["id"],
            childColumns = ["scheduleId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class BusStopSchedule(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val busStopId: Int,
    val busLineId: Int,
    val dayTypeId: Int,
    val scheduleId: Int
)
