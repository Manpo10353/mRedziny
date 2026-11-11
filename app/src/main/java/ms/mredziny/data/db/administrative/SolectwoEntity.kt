package ms.mredziny.data.db.administrative

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "solectwo", indices = [Index("localityId")])
data class SolectwoEntity(
    @PrimaryKey val id: Long,
    val localityId: Long,
    val name: String,
    val latitude: Double? = null,
    val longitude: Double? = null,
)
