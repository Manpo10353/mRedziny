package ms.mredziny.data.db.wastemanagement

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "waste_collection",
    indices = [Index(value = ["wasteRegionId", "collectionDate"])],
)
data class WasteCollectionEntity(
    @PrimaryKey val id: Long,
    val wasteRegionId: Long,
    val fraction: String,
    val collectionDate: String, // "2025-11-16"
)