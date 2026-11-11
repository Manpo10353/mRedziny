package ms.mredziny.data.db.communication

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "bus_stop")
data class BusStopEntity(
    @PrimaryKey
    @SerialName("id")
    val id: Long,

    @SerialName("name")
    val name: String,

    @ColumnInfo(name = "direction_name")
    @SerialName("direction_name")
    val directionName: String,

    @SerialName("city")
    val city: String,

    @SerialName("latitude")
    val latitude: Double,

    @SerialName("longitude")
    val longitude: Double,

    @ColumnInfo(name = "timetable_valid_from")
    @SerialName("timetable_valid_from")
    val timetableValidFrom: String? = null,

    @ColumnInfo(name = "direction_id")
    @SerialName("direction_id")
    val directionId: Long? = null,

    @ColumnInfo(name = "locality_id")
    @SerialName("locality_id")
    val localityId: Long? = null,
)
