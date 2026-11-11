package ms.mredziny.data.db.administrative

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "locality", indices = [Index("gminaId")])
data class LocalityEntity(
    @PrimaryKey val id: Long,
    val gminaId: Long,
    val name: String,
    val simc: String?,
    val latitude: Double? = null,
    val longitude: Double? = null,
)
