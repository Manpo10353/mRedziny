package ms.mredziny.data.db.wastemanagement

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "waste_region")
data class WasteRegionEntity(
    @PrimaryKey val id: Long,
    val name: String,
)