package ms.mredziny.data.db.wastemanagement

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(foreignKeys =
[
    ForeignKey(
    entity = Street::class,
    parentColumns = ["id"],
    childColumns = ["streetId"],
    onDelete = ForeignKey.CASCADE
    ),
    ForeignKey(
    entity = WasteSchedule::class,
    parentColumns = ["id"],
    childColumns = ["scheduleId"],
    onDelete = ForeignKey.CASCADE
    ),
    ForeignKey(
    entity = Date::class,
    parentColumns = ["id"],
    childColumns = ["dateId"],
    onDelete = ForeignKey.CASCADE
    )
]
)
data class WasteCollectionSchedule(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val streetId: Int,
    val dateId: Int,
    val scheduleId: Int
)
