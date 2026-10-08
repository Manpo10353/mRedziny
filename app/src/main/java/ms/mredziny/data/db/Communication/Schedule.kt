package ms.mredziny.data.db.Communication

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "schedule",
    foreignKeys = [
        ForeignKey(
            entity = BusLine::class,
            parentColumns = ["id"],
            childColumns = ["busLineId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
@TypeConverters(ScheduleConverter::class, PairConverter::class)
data class Schedule(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val busLineId: Int,
    val times: Map<Int, List<Pair<Int, String>>>
)