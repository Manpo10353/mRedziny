package ms.mredziny.data.db.communication

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "public_holiday")
data class PublicHolidayEntity(
    @PrimaryKey val holidayDate: String,
    val name: String,
)
