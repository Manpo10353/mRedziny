package ms.mredziny.data.db.wastemanagement

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity
data class WasteSchedule(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @TypeConverters(WasteCollectionScheduleConverter::class)
    val schedule: List<Pair<String,List<Int>>>
)
