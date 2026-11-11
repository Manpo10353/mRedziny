package ms.mredziny.data.db.wastemanagement

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "street",
    indices = [Index("wasteRegionId"), Index("localityId")],
)
data class StreetEntity(
    @PrimaryKey val id: Long,
    val localityId: Long,
    val wasteRegionId: Long,
    val name: String,
)